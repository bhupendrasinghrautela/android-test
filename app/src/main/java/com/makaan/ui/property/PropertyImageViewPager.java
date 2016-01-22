package com.makaan.ui.property;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.makaan.adapter.property.PropertyImagesPagerAdapter;
import com.makaan.response.listing.detail.ListingDetailImage;
import com.makaan.ui.anim.StackLikePagerTransform;

import java.util.List;

/**
 * Created by aishwarya on 19/01/16.
 */
public class PropertyImageViewPager extends ViewPager {

    private static final int H_PADDING = 120;
    private Context mContext;
    PropertyImagesPagerAdapter mPropertyImagesPagerAdapter;

    public PropertyImageViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public PropertyImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void bindView(){
        mPropertyImagesPagerAdapter = new PropertyImagesPagerAdapter(this,mContext);
        setAdapter(mPropertyImagesPagerAdapter);
        setClipToPadding(false);
        setPadding(10, 10, 10, 10);
        setPageTransformer(true, new StackLikePagerTransform());
    }

    public void setData(List<ListingDetailImage> listingDetailImageList){
        mPropertyImagesPagerAdapter.setData(listingDetailImageList);
        setCurrentItem(1, false);
    }
}