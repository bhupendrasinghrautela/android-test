package com.makaan.activity.buyerJourney;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.notification.NotificationAttributes;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context mContext;
    private List<NotificationAttributes> mNotifications = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public NotificationsAdapter(Context mContext, List<NotificationAttributes> mNotifications) {
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final NotificationsHolder mNotificationHolder = (NotificationsHolder)holder;
        mNotificationHolder.mNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        NotificationsHolder holder = new NotificationsHolder(v);
        return holder;
    }


}

