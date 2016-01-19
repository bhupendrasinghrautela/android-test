package com.makaan.ui.amenity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.makaan.R;
import com.makaan.adapter.AmenitiesPagerAdapter;
import com.makaan.adapter.listing.ListingPagerAdapter;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.listing.Listing;
import com.makaan.util.AnimUtil;

import java.util.List;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenityViewPager extends ViewPager {
    private static final int MARGIN = 30;
    private static final int H_PADDING = 120;
    private Context mContext;
    AmenitiesPagerAdapter mAmenityPagerAdapter;
    public AmenityViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public AmenityViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void bindView(){
        mAmenityPagerAdapter = new AmenitiesPagerAdapter(mContext);
        setAdapter(mAmenityPagerAdapter);
        setClipToPadding(false);
        setPageMargin(MARGIN);
        setPadding(0, 0, H_PADDING, 0);
        setOffscreenPageLimit(2);
    }

    public void setData(List<AmenityCluster> amenityClusters){
        mAmenityPagerAdapter.setData(amenityClusters);
    }

}