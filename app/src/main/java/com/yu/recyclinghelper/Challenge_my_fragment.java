package com.yu.recyclinghelper;

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
import com.google.gson.reflect.TypeToken;
import com.yu.recyclinghelper.Adapter.ChallengeAdapter;
import com.yu.recyclinghelper.Adapter.ChallengeItemClickListener;
import com.yu.recyclinghelper.VO.ChallengeData;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Challenge_my_fragment extends Fragment {

    //variable
    private ArrayList<ChallengeData> items = new ArrayList<>(); //recyclerView에 보이는 list
    private ArrayList<ChallengeData> list = new ArrayList<>();  //sharedpreference에 저장된 list
    public static final String MyPREFERENCES = "MyPrefs";
    private ChallengeAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For TEST
        SharedPreferences preferences = getActivity().getSharedPreferences("일회용품 안쓰기 챌린지", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("day", 6);
        editor.putString("date", "2000-01-01");
        editor.apply();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.challenge_my_fragment,container,false);
        recyclerView = view.findViewById(R.id.recyclerview_main_list);
        adapter = new ChallengeAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

        //itemList
        SharedPreferences prefs = getActivity().getSharedPreferences(MyPREFERENCES, 0);
        if(prefs.getString("myChallenge", null) != null){
            Log.v("my__",prefs.getAll().toString());
            Gson gson = new Gson();
            String json = prefs.getString("myChallenge", null);
            Type type = new TypeToken<ArrayList<ChallengeData>>(){}.getType();
            list = gson.fromJson(json, type);
            for(ChallengeData data : list){
                items.add(data);
            }
        }

        adapter.setOnItemClickListener(new ChallengeItemClickListener() {
            @Override
            public void onItemClick(ChallengeAdapter.ViewHolderPage holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", items.get(position).getTitle());
                Challenge_my_dialog dialog = new Challenge_my_dialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "dialog_my");
            }
        });

        return view;
    }

//    public void update(){
//        adapter = new ChallengeAdapter(items);
//        adapter.notifyDataSetChanged();
//        recyclerView.setAdapter(adapter);
//    }
}