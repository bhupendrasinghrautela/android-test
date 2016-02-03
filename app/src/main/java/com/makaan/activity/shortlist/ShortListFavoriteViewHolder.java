package com.makaan.activity.shortlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListFavoriteViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.iv_content)
    ImageView mImageViewBackground;
    @Bind(R.id.txt_get_call_back)
    TextView mTextViewGetCallBack;
    @Bind(R.id.tv_area)
    TextView mTextViewArea;
    @Bind(R.id.tv_locality)
    TextView mTextViewLocality;
    @Bind(R.id.tv_rs)
    TextView mTextViewRupees;
    @Bind(R.id.tv_price_value)
    TextView mTextViewPriceValue;
    @Bind(R.id.tv_price_unit)
    TextView mTextViewPriceUnit;
    @Bind(R.id.tv_onwards)
    TextView mTextViewOnwards;

    public ShortListFavoriteViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
