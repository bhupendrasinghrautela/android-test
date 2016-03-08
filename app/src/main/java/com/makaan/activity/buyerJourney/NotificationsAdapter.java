package com.makaan.activity.buyerJourney;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.database.NotificationDbHelper;
import com.makaan.notification.NotificationAttributes;
import com.makaan.notification.NotificationHelper;

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


        final NotificationCardViewHolder mNotificationCardViewHolder = (NotificationCardViewHolder)holder;
        mNotificationCardViewHolder.mNotificationCardView.bindView(mNotifications.get(position));
        mNotificationCardViewHolder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    NotificationDbHelper.deleteNotification(mContext, mNotifications.get(position).getNotificationId());
                    Toast.makeText(mContext, mContext.getString(R.string.notification_deleted)
                            , Toast.LENGTH_SHORT).show();
                    mNotifications.remove(position);
                    notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(mContext, mContext.getString(R.string.notification_deletion_error)
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNotificationCardViewHolder.mNotificationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationDbHelper.markNotificationRead(mContext, mNotifications.get(position).getNotificationId());
                Intent next = NotificationHelper.getIntentFromAttribute(mContext,mNotifications.get(position));
                if(next != null) {
                    mContext.startActivity(next);
                } else if(NotificationHelper.ScreenType.fromTypeId(mNotifications.get(position).getNotificationPayload().getScreenTypeId())
                        == NotificationHelper.ScreenType.BUYER_DASHBOARD) {
                    if(mContext instanceof BuyerJourneyActivity) {
                        ((NotificationCallbacks)mContext).loadDashboard();
                    }
                }
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        NotificationCardViewHolder holder = new NotificationCardViewHolder(v);
        return holder;
    }


    public interface NotificationCallbacks {
        void loadDashboard();
    }
}

