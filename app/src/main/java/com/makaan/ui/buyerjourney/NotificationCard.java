package com.makaan.ui.buyerjourney;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.jarvis.ui.cards.BaseCtaView;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.notification.NotificationAttributes;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.listing.BaseCardView;
import com.makaan.util.DateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunil on 07/03/16.
 */
public class NotificationCard extends BaseLinearLayout<NotificationAttributes> {

    @Bind(R.id.notification_time)
    public TextView mNotificationTime;
    @Bind(R.id.notification_detail)
    public TextView mNotificationDetail;
    @Bind(R.id.notification_msg)
    public TextView mNotificationMsg;

    @Bind(R.id.iv_notifications)
    public FadeInNetworkImageView mNotificationImage;

    private Context mContext;

    public NotificationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public NotificationCard(Context context) {
        super(context);
        mContext = context;
    }

    public NotificationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(NotificationAttributes item) {
        mNotificationDetail.setText(item.getMessage());
        mNotificationMsg.setText(item.getTitle());
        mNotificationTime.setText(DateUtil.getTime(item.getTimestamp()));

        if(!TextUtils.isEmpty(item.getNotificationPayload().getImageUrl())) {
            mNotificationImage.setVisibility(View.VISIBLE);
            mNotificationImage.setImageUrl(item.getNotificationPayload().getImageUrl(),
                    MakaanNetworkClient.getInstance().getImageLoader());
        }
    }


}