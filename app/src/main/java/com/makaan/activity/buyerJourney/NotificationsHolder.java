package com.makaan.activity.buyerJourney;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.notification_layout)
    public LinearLayout mNotificationLayout;
    public NotificationsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}


