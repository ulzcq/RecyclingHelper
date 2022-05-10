package com.yu.recyclinghelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import com.google.gson.Gson;
import com.yu.recyclinghelper.Adapter.VPAdapter;
import com.yu.recyclinghelper.VO.ChallengeData;

import java.util.ArrayList;

import static com.yu.recyclinghelper.Challenge_all_dialog.MyPreferences;

public class Challenge_fragment extends Fragment {
    private static final String TAG = "Challenge_fragment";

    //variable
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VPAdapter adapter;
    Fragment challenge_all = new Challenge_all_fragment();
    Fragment challenge_my = new Challenge_my_fragment();

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater. inflate(R.layout.challenge_fragment, container, false);
        Log.v("ing","view");
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.pager);
        adapter = new VPAdapter(this.getChildFragmentManager());

        adapter.AddFragement(new Challenge_all_fragment(), "진행중인 챌린지");
        adapter.AddFragement(new Challenge_my_fragment(), "나의 챌린지");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.v("ing", "resume");
    }
}