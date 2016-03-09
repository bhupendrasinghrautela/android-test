package com.makaan.ui.locality;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.ui.listing.BaseCardView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 17/1/16.
 */
public class NearbyLocalitiesView extends BaseCardView {
    @Bind(R.id.locality_name)TextView mName;
    @Bind(R.id.median_price)TextView mNameInfo;
    @Bind(R.id.sale_count)TextView mSaleCount;
    @Bind(R.id.rent_count)TextView mRentCount;
    @Bind(R.id.view_details)TextView mViewDetails;
    //@Bind(R.id.locality_image)FadeInNetworkImageView mLocalityImage;

    public NearbyLocalitiesView(Context context) {
        super(context);
    }

    public NearbyLocalitiesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NearbyLocalitiesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, Object item) {
    }

    public void bindLocalityData(String value){
        if(value != null) {
            mName.setText(value.toLowerCase());
        }
    }

    @OnClick(R.id.view_details)
    public void viewDetails(){

    }


}
