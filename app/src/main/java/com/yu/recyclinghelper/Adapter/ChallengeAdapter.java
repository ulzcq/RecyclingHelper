package com.yu.recyclinghelper.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.yu.recyclinghelper.R;
import com.yu.recyclinghelper.VO.ChallengeData;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolderPage>{
    private ChallengeItemClickListener listener;
    private ArrayList<ChallengeData> list;
    private int count = 0;

    public ChallengeAdapter(ArrayList<ChallengeData> data){
        this.list = data;
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolderPage onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.challenge_item, parent, false);
        final ViewHolderPage viewHolderPage = new ViewHolderPage(view);
        return viewHolderPage;
    }
    public void setOnItemClickListener(ChallengeItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolderPage holder, int position) {
        if(holder instanceof ViewHolderPage){
            ViewHolderPage viewHolderPage = (ViewHolderPage) holder;
            viewHolderPage.onBind(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(ArrayList<ChallengeData> data){
        this.count = count;
        notifyDataSetChanged();
    }

    public class ViewHolderPage extends RecyclerView.ViewHolder{
        public LinearLayout itemContact;
        private TextView tvTitle;
        private TextView tvContent;
        private ImageView image;
        private ChallengeData item;

        public ViewHolderPage(@NonNull @NotNull View itemView) {
            super(itemView);
            itemContact = itemView.findViewById(R.id.itemID);
            tvTitle = itemView.findViewById(R.id.challengeTitle);
            tvContent = itemView.findViewById(R.id.challengeContent);
            image = itemView.findViewById(R.id.challengeImage);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolderPage.this, v, position);
                    }
                }
            });
        }

        public void onBind(ChallengeData item){
            this.item = item;
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            image.setImageResource(item.getResId());
        }
    }
}
