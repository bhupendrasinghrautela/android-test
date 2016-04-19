package com.makaan.ui.amenity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.makaan.adapter.AmenitiesPagerAdapter;
import com.makaan.fragment.overview.OverviewFragment.OverviewActivityCallbacks;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.amenity.AmenityCardView.AmenityCardViewCallBack;

import java.util.List;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenityViewPager extends ViewPager implements AmenityCardViewCallBack {
    private static final int MARGIN = 30;
    private static final int H_PADDING = 120;
    private Context mContext;
    private OverviewActivityCallbacks mCallBack;

    AmenitiesPagerAdapter mAmenityPagerAdapter;
    public AmenityViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public AmenityViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void bindView(OverviewActivityCallbacks showMapCallback){
        mAmenityPagerAdapter = new AmenitiesPagerAdapter(mContext);
        mCallBack = showMapCallback;
        mAmenityPagerAdapter.setCallback(this);
        setAdapter(mAmenityPagerAdapter);
        setClipToPadding(false);
        setPageMargin(MARGIN);
        setPadding(10, 10, H_PADDING, 10);
        setOffscreenPageLimit(2);
    }

    public void setData(List<AmenityCluster> amenityClusters){
        mAmenityPagerAdapter.setData(amenityClusters);
    }

    @Override
    public void onAmenityDistanceClicked(String placeName) {
        mCallBack.showMapFragmentWithSpecificAmenity(this.getCurrentItem(),placeName);
    }
}