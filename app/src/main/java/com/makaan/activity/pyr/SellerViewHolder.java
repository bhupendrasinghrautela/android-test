package com.makaan.activity.pyr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by makaanuser on 6/1/16.
 */
public class SellerViewHolder extends RecyclerView.ViewHolder{

    @Bind(R.id.cb_tick)
    CheckBox mCheckBoxTick;
    @Bind(R.id.iv_seller_image)
    TextView mTextSellerImage;
    @Bind(R.id.seller_image)
    CircleImageView mSellerImage;
    @Bind(R.id.tv_seller_name)
    TextView mSellerName;
    @Bind(R.id.tv_details_expertise)
    TextView mTextViewExpertise;
    @Bind(R.id.seller_ratingbar)
    RatingBar mSellerRatingBar;
    private boolean isChecked=false;

    public SellerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
