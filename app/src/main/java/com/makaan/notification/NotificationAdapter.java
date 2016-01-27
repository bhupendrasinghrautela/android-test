package com.makaan.notification;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context mContext;
    private List<NotificationAttributes> mNotifications = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public NotificationAdapter(Context mContext, List<NotificationAttributes> mNotifications ) {
        this.mContext=mContext;
        this.mNotifications=mNotifications;
    }

    public void setData(List<NotificationAttributes> data){
        mNotifications = data;
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        //NotificationCardViewHolder vh = new NotificationCardViewHolder(v);
        return null;
    }


}

