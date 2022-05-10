package com.yu.recyclinghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Challenge_my_dialog extends androidx.fragment.app.DialogFragment {

    //variable
    private List<Button> days = new ArrayList<>();
    private Button btn;
    private int dayNumber = 0;
    private final int GET_GALLERY_IMAGE = 200;
    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    private SharedPreferences sharedPreferences;
    TextView tvTitle;
    ImageButton exitBtn;
    Button retryBtn;
    TextView tvSuccess;

    public Challenge_my_dialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(getArguments().getString("title"), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_my_dialog, container, false);
        setButtons(view);

        dayNumber = sharedPreferences.getInt("day", 0);
        //test
//        dayNumber = 6;
        String date = sharedPreferences.getString("date", null);
        if(dayNumber == 7){
            success();
        }
        else if(dayNumber > -1 && dayNumber < 7) {
        //하루에 한번만 가능하게
            //다른 날짜에 도전하면 토스트 메시지 다르게 띄우기
            if(date == null || !date.equals(this.today)){
                btn = days.get(dayNumber);
                dayClick();
                for(int i = 6; i > dayNumber; i--){
                    days.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(), (dayNumber+1)+"번째 날에 도전하세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else {
                for(int i = 6; i > dayNumber -1; i--){
                    days.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(), "오늘 챌린지는 이미 도전하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        //완료된 날짜 토스트
        for(int i = 0; i < dayNumber; i++){
            days.get(i).setText("완료");
            days.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(), "완료된 날짜입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //종료버튼
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(Challenge_my_dialog.this).attach(Challenge_my_dialog.this);
                ft.commit();
            }
        });

        return view;
    }

    public void setButtons(View view){
        tvTitle = view.findViewById(R.id.dialogTitle);
        tvTitle.setText(getArguments().getString("title"));
        days.add((Button) view.findViewById(R.id.challenge_first));
        days.add((Button) view.findViewById(R.id.challenge_second));
        days.add((Button) view.findViewById(R.id.challenge_third));
        days.add((Button) view.findViewById(R.id.challenge_fourth));
        days.add((Button) view.findViewById(R.id.challenge_fifth));
        days.add((Button) view.findViewById(R.id.challenge_sixth));
        days.add((Button) view.findViewById(R.id.challenge_last));
        exitBtn = (ImageButton) view.findViewById(R.id.btn_exit);
        retryBtn = (Button) view.findViewById(R.id.btn_retry);
        tvSuccess = view.findViewById(R.id.tvSuccess);
    }

    public void dayClick(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            //버튼 완료로 바꾸기
            btn.setEnabled(false);
            btn.setText("완료");
            //날짜 기록
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("day", ++dayNumber);
            String setDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
            editor.putString("date", setDate);
            Log.v("dialog", setDate);
            editor.apply();

            //7일 성공시 성공 버튼 활성화
            if(dayNumber == 7){
                Toast.makeText(getContext().getApplicationContext(), "7일동안 성공했어요! 대단해요👏", Toast.LENGTH_LONG);
                success();
            }else if(dayNumber > -1 && dayNumber <7){
                //성공 메시지
                Toast.makeText(getActivity().getApplicationContext(), "오늘의 챌린지 도전성공!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void success(){
        retryBtn.setVisibility(View.VISIBLE);
        retryBtn.setEnabled(true);
        retryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("day");
                editor.remove("date");
                editor.commit();
                Toast.makeText(getActivity().getApplicationContext(), "다시 도전하는 당신! 😘😘", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        tvSuccess.setVisibility(View.VISIBLE);
    }
}