package com.makaan.activity.sitevisit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.ui.view.CustomRatingBar;
import com.pkmmte.view.CircularImageView;

/**
 * Created by aishwarya on 18/02/16.
 */
public class SiteVisitViewHolder extends RecyclerView.ViewHolder {
    public FadeInNetworkImageView mMainImage;
    public TextView mAddress;
    public TextView mName;
    public TextView mSellerText;
    public TextView mSiteVisitDate;
    public TextView mSiteVisitTime;
    public TextView mDirection;
    public TextView mCallNow;
    public CustomRatingBar mRating;
    public CircularImageView mSellerImage;
    private int position;
    private SiteVisitCallbacks callbacks;

    public SiteVisitViewHolder(View itemView) {
        super(itemView);
        mMainImage = (FadeInNetworkImageView) itemView.findViewById(R.id.iv_content);
        mAddress = (TextView) itemView.findViewById(R.id.address);
        mName = (TextView) itemView.findViewById(R.id.name);
        mSiteVisitDate = (TextView) itemView.findViewById(R.id.date);
        mSiteVisitTime = (TextView) itemView.findViewById(R.id.time);
        mDirection = (TextView) itemView.findViewById(R.id.direction);
        mCallNow = (TextView) itemView.findViewById(R.id.call_now);
        mSellerImage = (CircularImageView) itemView.findViewById(R.id.seller_image_view);
        mSellerText = (TextView) itemView.findViewById(R.id.seller_logo_text_view);
        mRating = (CustomRatingBar) itemView.findViewById(R.id.seller_rating);
        mDirection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.openDirections(position);
            }
        });
        mCallNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.callNumber(position);
            }
        });
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setCallback(SiteVisitCallbacks callback){
        this.callbacks = callback;
    }
}
