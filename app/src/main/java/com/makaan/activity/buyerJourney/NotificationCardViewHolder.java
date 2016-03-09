package com.makaan.activity.buyerJourney;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.ui.buyerjourney.NotificationCard;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationCardViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.notification_card_layout)
    public NotificationCard mNotificationCardView;

    @Bind(R.id.iv_delete)
    public ImageView mImageViewDelete;


    public NotificationCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}


