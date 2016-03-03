package com.makaan.activity.sitevisit;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.ui.view.CustomRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public CircleImageView mSellerImage;
    private int position;
    private SiteVisitCallbacks callbacks;

    private View mDirectionLayout;
    private View mCallNowLayout;

    public SiteVisitViewHolder(View itemView) {
        super(itemView);
        mMainImage = (FadeInNetworkImageView) itemView.findViewById(R.id.iv_content);
        mAddress = (TextView) itemView.findViewById(R.id.address);
        mName = (TextView) itemView.findViewById(R.id.name);
        mSiteVisitDate = (TextView) itemView.findViewById(R.id.date);
        mSiteVisitTime = (TextView) itemView.findViewById(R.id.time);
        mDirection = (TextView) itemView.findViewById(R.id.direction);
        mCallNow = (TextView) itemView.findViewById(R.id.call_now);
        mSellerImage = (CircleImageView) itemView.findViewById(R.id.seller_image_view);
        mSellerText = (TextView) itemView.findViewById(R.id.seller_logo_text_view);
        mRating = (CustomRatingBar) itemView.findViewById(R.id.seller_rating);

        mDirectionLayout = itemView.findViewById(R.id.direction_layout);
        mCallNowLayout = itemView.findViewById(R.id.call_now_layout);

        mDirectionLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.openDirections(position);
            }
        });
        mCallNowLayout.setOnClickListener(new OnClickListener() {
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
