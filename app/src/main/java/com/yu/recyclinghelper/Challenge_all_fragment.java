package com.yu.recyclinghelper;

import static com.yu.recyclinghelper.Challenge_all_dialog.MyPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yu.recyclinghelper.Adapter.ChallengeAdapter;
import com.yu.recyclinghelper.Adapter.ChallengeItemClickListener;
import com.yu.recyclinghelper.VO.ChallengeData;

import java.util.ArrayList;

public class Challenge_all_fragment extends Fragment {
    private ArrayList<ChallengeData> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.challenge_all_fragment, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerview_main_list);
        ChallengeAdapter vpAdapter = new ChallengeAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(vpAdapter);

        vpAdapter.setOnItemClickListener(new ChallengeItemClickListener() {
            @Override
            public void onItemClick(ChallengeAdapter.ViewHolderPage holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", items.get(position).getTitle());
                bundle.putString("content", items.get(position).getContent());
                bundle.putInt("image", items.get(position).getResId());
                Challenge_all_dialog dialoging = new Challenge_all_dialog();
                dialoging.setArguments(bundle);
                dialoging.show(getActivity().getSupportFragmentManager(), "dialog_all");
            }
        });

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.v("all","resume");
        //itemList
        items.add(new ChallengeData("텀블러 사용 챌린지", "텀블러 사용하고 플라스틱 컵 사용 줄이자!", R.drawable.tumbler));
        items.add(new ChallengeData("자전거 이용 챌린지", "가까운 거리는 자전거로 이동하고 운동도 하고!", R.drawable.bicycle));
        items.add(new ChallengeData("식물 키우기 챌린지", "반려식물 키우며 이산화탄소 줄이고 \n 내 방 인테리어도 화사하게!", R.drawable.planting));
        items.add(new ChallengeData("채식 챌린지", "채식 도전하고 온실가스 줄이자!", R.drawable.vegetable));
        items.add(ChallengeData.builder().title("일회용품 안쓰기 챌린지").content("하루동안 일회용품 쓰지 않고 살아보아요!").resId(R.drawable.disposable).build());
        items.add(new ChallengeData("용기내 챌린지", "용기내서 다회용 용기에 음식 포장하자!", R.drawable.lunch_box));

        ///다시 
        /*SharedPreferences prefs = getActivity().getSharedPreferences(MyPreferences, 0);
        SharedPreferences.Editor editing = prefs.edit();
        ArrayList<ChallengeData> test = new ArrayList<>();
        test.add(ChallengeData.builder().title("일회용품 안쓰기 챌린지").content("하루동안 일회용품 쓰지 않고 살아보아요!").resId(R.drawable.disposable).build());
        Gson gson = new Gson();
        String json = gson.toJson(test);
        editing.putString("myChallenge", json);
        editing.apply();*/
    }
}