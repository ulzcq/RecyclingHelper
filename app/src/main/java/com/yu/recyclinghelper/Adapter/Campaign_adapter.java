package com.yu.recyclinghelper.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yu.recyclinghelper.R;
import com.yu.recyclinghelper.VO.CampaignData;

import java.util.ArrayList;

public class Campaign_adapter extends RecyclerView.Adapter<Campaign_adapter.ViewHolder> {
    private ArrayList<CampaignData> listData = new ArrayList<CampaignData>();
    private CampaignItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.campaign_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CampaignData item = listData.get(position);
        holder.setItem(item);
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }
    public void setOnItemClickListener(CampaignItemClickListener listener){
        this.listener = listener;
    }
    public void onItemClick(ViewHolder viewHolder, View view, int position){
        if(listener != null) {
            listener.onItemClick(viewHolder, view, position);
        }
    }

    public void addItem(CampaignData campaignData){
        listData.add(campaignData);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView campaignTitle;
        private TextView campaignContent;
        private ImageView campaignImage;

        public ViewHolder(View itemView){
            super(itemView);

            campaignTitle = itemView.findViewById(R.id.campaignTitle);
            campaignContent = itemView.findViewById(R.id.campaignContent);
            campaignImage = itemView.findViewById(R.id.campaignImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(CampaignData campaignData) {
            campaignTitle.setText(campaignData.getTitle());
            campaignContent.setText(campaignData.getContent());
            campaignImage.setImageResource(campaignData.getResId());

        }
    }
    public CampaignData getItem(int position) {
        return listData.get(position);
    }
}
