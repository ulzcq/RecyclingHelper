package com.yu.recyclinghelper;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Mark_explain_dialog extends androidx.fragment.app.DialogFragment {
    ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.mark_explain_dialog, container, false);
        //TODO: 실행화면
        image = view.findViewById(R.id.markImage);
        setCancelable(false);
        Bundle bundle = getArguments();
        String mark = bundle.getString("mark");
        if(mark.equals("plastic")){
            image.setImageResource(R.drawable.plastic);
        }else if(mark.equals("vinyl")){
            image.setImageResource(R.drawable.vinyl);
        }else if(mark.equals("pet")){
            image.setImageResource(R.drawable.pet);
        }else if(mark.equals("glass")){
            image.setImageResource(R.drawable.glass);
        }else if(mark.equals("can")){
            image.setImageResource(R.drawable.can);
        }else if(mark.equals("paper")){
            image.setImageResource(R.drawable.paper);
        }else if(mark.equals("null")){
            image.setImageResource(R.drawable.vinyl);
        }
        ImageButton exit = view.findViewById(R.id.btn_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }
}