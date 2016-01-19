package com.makaan.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.amenity.AmenityCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenitiesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<AmenityCluster> mItems = new ArrayList<>();

    public AmenitiesPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<AmenityCluster> list){
        mItems.clear();
        mItems.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AmenityCardView amenityCardView =
                (AmenityCardView) mLayoutInflater.inflate(R.layout.amenity_cluster_view, null);

        amenityCardView.bindView(mContext, mItems.get(position));
        container.addView(amenityCardView);
        return amenityCardView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}