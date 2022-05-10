#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/opencv.hpp>

#include <iostream>
#include <fstream>
#include <iomanip>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <opencv2/imgproc/types_c.h>
#include <android/log.h>

using namespace std;
using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_yu_recyclinghelper_Camera_1Activity_templateMatchingJNI(JNIEnv *env, jobject thiz,
                                                              jlong template_image,
                                                              jlong input_image, jlong output_image,
                                                              jint th1,
                                                              jint th2) {

    Mat &templateMat = *(Mat *) template_image;
    Mat &inputMat = *(Mat *) input_image;
    Mat &outputMat = *(Mat *) output_image;
    inputMat.copyTo(outputMat);

    Mat result;
    Mat finalMat;

    int result_cols = inputMat.cols - templateMat.cols + 1;
    int result_rows = inputMat.rows - templateMat.rows + 1;
    result.create(result_rows, result_cols, CV_32F);

    ///마크 영역 찾기 위한 전처리
    cvtColor(inputMat, inputMat, COLOR_BGR2GRAY);// GrayScale 글자 탐지를 쉽게 하기 위함

    //shape = 0: Rect(직사각형) 1: Cross(십자형,Difference 필터) 2: Ellipse(타원형,라플라시안 필터)
    //anchor = 커널 중심, default: (-1,-1)
    Mat element = getStructuringElement(MORPH_RECT, Size(3, 3), Point(-1,-1));
    morphologyEx(inputMat, inputMat, cv::MORPH_GRADIENT, element);//Morph Gradient, Dilate(팽창) - Erosion(침식) : 외곽선을 더 정확하게 추출

    cv::adaptiveThreshold(inputMat, inputMat, 150, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 9,5); //Binary

    ///템플릿 매칭
    double minVal, maxVal;
    Point minLoc, maxLoc, matchLoc;

    cvtColor(templateMat, templateMat, COLOR_BGR2GRAY); //템플릿 이미지 채널 맞추기 위함
    matchTemplate(inputMat, templateMat, result, TM_CCOEFF_NORMED); //정규화 상관 계수
    normalize(result, result, 0, 1, NORM_MINMAX, -1, Mat());
    minMaxLoc(result, &minVal, &maxVal, &minLoc, &maxLoc, Mat()); //되는거
    matchLoc = minLoc;
    __android_log_print(ANDROID_LOG_DEBUG, "JNI", "matchLoc.y = %d, matxhLoc.x = %d", matchLoc.y, matchLoc.x);

    outputMat = outputMat(Range(matchLoc.y, matchLoc.y + templateMat.rows),
                          Range(matchLoc.x, matchLoc.x + templateMat.cols));

}

extern "C"
JNIEXPORT void JNICALL
Java_com_yu_recyclinghelper_Camera_1Activity_extractROIJNI(JNIEnv *env, jobject thiz, jlong input_image, jlong output_image, jlong text_image) {
    Mat &inputMat = *(Mat *) input_image;
    Mat &outputMat = *(Mat *) output_image;
    Mat &textMat = *(Mat *) text_image;

    textMat = cv::Mat::zeros(1, 1, CV_32F);

    Mat resultMat;
    Mat croppedRoi;
    inputMat.copyTo(outputMat); //전처리하기 위해 원본이미지 복사
    inputMat.copyTo(resultMat); //잘라내기 위해 원본이미지 복사

    ///글자 영역 추출하기 위한 전처리
    cvtColor(outputMat, outputMat, COLOR_BGR2GRAY);
    GaussianBlur(outputMat, outputMat, Size(3, 3), 0);
    Canny(outputMat, outputMat, 10, 100, 3, true);//바이너리 이미지(흑백)로 변경

    ///화살표 제거
    vector<vector<Point>> contours;
    findContours(outputMat, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE); //윤곽선을 찾아서 contours 벡터 안에 Point 정보를 저장

    for (int i = 0; i < contours.size(); i++) {

        Rect rect = boundingRect(contours[i]);
        __android_log_print(ANDROID_LOG_DEBUG, "JNI", "extractROIJNI 네번째 오류");

//        float ratio = (float) rect.width / (float) rect.height;

        if ((6 < rect.width && rect.width < 120) && (6 < rect.height && rect.height < 120)) {
            __android_log_print(ANDROID_LOG_DEBUG, "JNI", "rect.width = %f, rect.height = %f",
                                rect.width, rect.height);
        } else {
            fillPoly(outputMat, contours[i], Scalar(0, 0, 0), LINE_8);
        }
    }

    /// 1. 다시 글자끼리 묶는 전처리
    //Morph Closing : 팽창 후 침식 수행. 작은 구멍과 차이를 메꿈. 글자와 글자처럼 사이가 먼 픽셀이 적당히 잘 뭉쳐져 글자 부분이 그룹(한 덩어리)으로 잘 묶인다. 이렇게 되면 글자에 해당하는 영역을 원본 이미지에서 어느 정도는 쉽게 분리해 찾을 수 있다.작은 홀을 메우거나 오브젝트를 연결
    Mat close_element = getStructuringElement(CV_SHAPE_RECT, Size(5, 7), Point(-1, -1));//2x2
    morphologyEx(outputMat, outputMat, cv::MORPH_CLOSE, close_element);

    //Dilation : 떨어진 오브젝트들을 붙이는데 사용, 더 굵게 처리
    Mat matKernel = getStructuringElement(MORPH_RECT, Size(3, 3), Point(0, 0));
    dilate(outputMat, outputMat, matKernel, Point(-1, -1), 5);

    /// 2. 묶인 글자 contours만 자르기(묶였으니까 가로로 긴 것만)
    vector<vector<Point>> contours2;
    findContours(outputMat, contours2, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    vector<Mat> imageVector; //자른 이미지가 들어갈 Mat 배열
    int text_exist = 0;

    for (int i = 0; i < contours2.size(); i++) {
        Rect rect = boundingRect(contours2[i]);

        float ratio = (float) rect.width / (float) rect.height;
        if (ratio > 1.2) {
            text_exist = 1;
            rectangle(inputMat, rect, Scalar(255, 0, 0), 2); //원본이미지에 직사각형 표시

            croppedRoi = resultMat(rect); //원본이미지에서 글자부분만 자르기

            if (croppedRoi.rows >= textMat.rows) {
                croppedRoi.copyTo(textMat);
            }
        }
    }

    if (text_exist == 0) {
        resultMat.copyTo(textMat);
    }

    ///텍스트 ROI 전처리
    cvtColor(textMat, textMat, COLOR_BGR2GRAY);

}