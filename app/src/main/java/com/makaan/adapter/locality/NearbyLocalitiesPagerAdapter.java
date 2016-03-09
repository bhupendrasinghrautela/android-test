package com.makaan.adapter.locality;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.locality.NearbyLocalitiesView;

/**
 * Created by proptiger on 17/1/16.
 */
public class NearbyLocalitiesPagerAdapter extends PagerAdapter {
    private Context mContext;
    private int itemSize;

    public NearbyLocalitiesPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setLocalitiesData(){

    }

    private void setItemCount(int size) {

    }

    @Override
    public int getCount() {
        return itemSize;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View markerInfoWindow =
                mLayoutInflater.inflate(R.layout.nearby_localities_layout, null);

        NearbyLocalitiesView nearbyLocalitiesView = (NearbyLocalitiesView) markerInfoWindow;
        //nearbyLocalitiesView.bindView(mContext, arr);
        container.addView(markerInfoWindow);
        return markerInfoWindow;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
