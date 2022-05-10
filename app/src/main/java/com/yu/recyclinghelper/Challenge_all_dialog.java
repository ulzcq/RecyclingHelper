package com.yu.recyclinghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yu.recyclinghelper.VO.ChallengeData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Challenge_all_dialog extends androidx.fragment.app.DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //variable
    SharedPreferences prefs, sharedPreferences;
    public static final String MyPreferences = "MyPrefs";
    ArrayList<ChallengeData> items = new ArrayList<>();
    private String title ="", content="";
    private int image = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_all_dialog,container);
        //initialize bundle
        title = getArguments().getString("title");
        content = getArguments().getString("content");
        image = getArguments().getInt("image");

        //initialize xml
        TextView tvExplain = view.findViewById(R.id.joinchallengeContent);
        ImageView imageView = view.findViewById(R.id.challengeImage);
        imageView.setImageResource(image);
        tvExplain.setText(getArguments().getString("content"));
        Button joinChallengeBtn = (Button) view.findViewById(R.id.btn_joinChallenge);

        //Click Event
        joinChallengeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getContext(), MainActivity.class);
                //휴대폰 내부 저장
                prefs = getActivity().getSharedPreferences(MyPreferences, 0);
                SharedPreferences.Editor editor = prefs.edit();
                getElements();
                //title 비교
                boolean result = compareTitle();

                //false일 때 title이 같지 않음
                if(result != true){
                    items.add(new ChallengeData(title, content, image));
                    Gson gson = new Gson();
                    String json = gson.toJson(items);
                    editor.putString("myChallenge", json);
                    editor.apply();
                    Log.v("all", getArguments().getString("content"));
                    startActivity(intent);
                    Challenge_my_fragment myFragment = new Challenge_my_fragment();
//                    myFragment.update();
                    Toast toasting = Toast.makeText(getActivity().getApplicationContext(), "나의 챌린지에 추가 성공!", Toast.LENGTH_SHORT);
                    toasting.show();
                    dismiss();
                }
                //true일 때, title이 같음
                else{
                    Toast toasting = Toast.makeText(getActivity().getApplicationContext(), "이미 존재하는 챌린지입니다.", Toast.LENGTH_SHORT);
                    toasting.show();
                }

            }
        });
        return view;
    }

    //저장된 값 가져오기
    public void getElements() {
        prefs = getActivity().getSharedPreferences(MyPreferences, 0);
        if(prefs.getString("myChallenge", null) != null){
            Log.v("all",prefs.getAll().toString());
            Gson gson = new Gson();
            String json = prefs.getString("myChallenge", null);
            Type type = new TypeToken<ArrayList<ChallengeData>>(){}.getType();
            items = gson.fromJson(json, type);
        }
    }

    public boolean compareTitle(){
        boolean result = false;
        if(items != null) {
            for (ChallengeData item : items) {
                //같으면 true
                Log.v("all",title+" "+String.valueOf(item.getTitle().equals(title)));

                if (item.getTitle().equals(title))
                    return true;
                else result = false;
            }
        }
        Log.v("compare", String.valueOf(result));
        return result;
    }
}