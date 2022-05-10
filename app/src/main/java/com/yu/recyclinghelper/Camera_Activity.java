package com.yu.recyclinghelper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Camera_Activity extends AppCompatActivity {

    private TessBaseAPI tessBaseAPI;
    private String dataPath = ""; //언어데이터가 있는 경로
    private final String[] languageList = {"eng", "kor"}; // 언어

    //view
    private CameraSurfaceView surfaceView;
    private Button button;
//    private ImageView resultImageView; // 원본
//    private ImageView result2ImageView;//1차 전처리 결과
//    private ImageView result3ImageView; // 템플릿 매칭 결과
//    private ImageView result4ImageView; // 2차 전처리 결과
//    private ImageView result5ImageView; // 텍스트 roi

    //bitmap image
    private Bitmap usedImage;
    private Bitmap templateImage;

    // permission
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};

    static final int resizeWidth = 300;
    private boolean mIsOpenCVReady = false;

    public native void templateMatchingJNI(long templateImage, long inputImage, long outputImage, int th1, int th2);
    public native void extractROIJNI(long inputImage, long outputImage, long textImage);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_activity);

        //permission 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        surfaceView = findViewById(R.id.surfaceView);
//        resultImageView = findViewById(R.id.resultImageView);
//        result2ImageView = findViewById(R.id.result2ImageView);
//        result3ImageView = findViewById(R.id.result3ImageView);
//        result4ImageView = findViewById(R.id.result4ImageView);
//        result5ImageView = findViewById(R.id.result5ImageView);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });

        //글자인식
        Tesseract();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mIsOpenCVReady = true;
        }
    }

    public void onDestroy() {
        super.onDestroy();

//        usedImage.recycle();
        if (usedImage != null) {
            usedImage = null;
        }
    }

    private Bitmap resizeTemplateImage(int resizeWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.template_edge, options);
        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        return Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
    }

    private void capture() {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                usedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if(usedImage == null){
                    Log.e("ERR", "Failed to decode Byte Array");
                    return;
                }
                usedImage = GetRotatedBitmap(usedImage, 90);
                usedImage = cropBitmap(usedImage, 400, 400);
//                resultImageView.setImageBitmap(usedImage); //캡쳐된 이미지 띄우기

                button.setEnabled(false);
                button.setText("텍스트 인식중...");

                usedImage = preprocessImage(usedImage); //전처리
                new AsyncTess().execute(usedImage); //글자인식

                camera.startPreview();
            }
        });
    }

    public Bitmap cropBitmap(Bitmap bitmap, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        // 이미지를 crop 할 좌상단 좌표
        int x = (originWidth - width) / 2;
        int y = (originHeight - height) / 2;

        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    public Bitmap preprocessImage(Bitmap capturedImage) {
        if (!mIsOpenCVReady) {
            return null;
        }

        //템플릿 이미지 압축
        templateImage = resizeTemplateImage(resizeWidth);

        //마크 추출
        Mat result = getMarkRoiMatUsingJNI(capturedImage);

        Bitmap matchingResultImage = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888); //마크영역만 잘라낸 이미지의 크기로 bitmap 만들기
        Utils.matToBitmap(result, matchingResultImage);
//        result3ImageView.setImageBitmap(matchingResultImage);

        //텍스트 roi 추출
        return getTextRoiImageUsingJNI(result, matchingResultImage);
    }

    @NotNull
    private Mat getMarkRoiMatUsingJNI(Bitmap capturedImage) {
        Mat templ = new Mat();
        Mat src = new Mat();
        Mat result = new Mat();

        Utils.bitmapToMat(templateImage, templ);
        Utils.bitmapToMat(capturedImage, src);

        templateMatchingJNI(templ.getNativeObjAddr(), src.getNativeObjAddr(), result.getNativeObjAddr(), 50, 150);

//        Bitmap preprocessedImage = capturedImage.copy(capturedImage.getConfig(), true);
//        Utils.matToBitmap(src, preprocessedImage);
//        result2ImageView.setImageBitmap(preprocessedImage); //템플릿 매칭을 위한 전처리 결과 이미지

        return result;
    }

    @NotNull
    private Bitmap getTextRoiImageUsingJNI(Mat result, Bitmap croppedImage) {
        Mat roi = new Mat();
        Mat text = new Mat();

        extractROIJNI(result.getNativeObjAddr(), roi.getNativeObjAddr(), text.getNativeObjAddr());

//        Bitmap preprocessedImage = croppedImage.copy(croppedImage.getConfig(), true);
//        Utils.matToBitmap(roi, preprocessedImage);
//        result4ImageView.setImageBitmap(preprocessedImage); // 텍스트 추출을 위한 전처리 결과 이미지

        Bitmap textRoiImage = Bitmap.createBitmap(text.cols(), text.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(text, textRoiImage);
//        result5ImageView.setImageBitmap(textRoiImage); // 텍스트 추출 결과 이미지

        return textRoiImage;
    }

    public void Tesseract() {
        dataPath = getFilesDir() + "/tesseract/"; //파일 경로

        String language = "";
        for (String lang : languageList) {
            checkLanguageFile(new File(dataPath + "tessdata/"), lang);
            language += lang + "+";
        }
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(dataPath, language);
    }

    boolean checkLanguageFile(File dir, String language) {
        if (!dir.exists() && dir.mkdirs())
            createFiles(language);
        else if (dir.exists()) {
            String dataFilePath = dataPath + "tessdata/" + language + ".traineddata";
            File langDataFile = new File(dataFilePath);
            if (!langDataFile.exists())
                createFiles(language);
        }
        return true;
    }

    private void createFiles(String language) {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            String filepath = dataPath + "/tessdata/" + language + ".traineddata";
            inputStream = assetMgr.open("tessdata/" + language + ".traineddata");

            outputStream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            textClassification(result);

            button.setEnabled(true);
            button.setText("텍스트 인식");

        }
    }

    public void textClassification(String text) {
        if (text.contains("플") || text.contains("라") || text.contains("스") || text.contains("틱")) {
            //TODO: 플라스틱 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "plastic");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else if (text.contains("비") || text.contains("닐")) {
            //TODO: 비닐 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "vinyl");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else if (text.contains("종") || text.contains("이")) {
            //TODO: 종이 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "paper");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else if (text.contains("캔")) {
            //TODO: 캔 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "can");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else if (text.contains("페") || text.contains("트")) {
            //TODO: 페트 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "pet");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else if (text.contains("유") || text.contains("리")) {
            //TODO: 유리 정보 창 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("mark", "glass");
            Mark_explain_dialog dialog = new Mark_explain_dialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "dialog_markImage");
        } else {
            //실패
            Toast.makeText(Camera_Activity.this, "다시 찍어보세요!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("실행을 위해 권한 허가가 필요합니다.");
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Camera_Activity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}