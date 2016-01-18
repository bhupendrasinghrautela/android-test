package com.makaan.adapter.locality;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.locality.LocalityApartmentsView;

/**
 * Created by proptiger on 18/1/16.
 */
public class LocalityApartmentsPagerAdapter  extends PagerAdapter {
    private Context mContext;
    private int itemSize;

    public LocalityApartmentsPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setApartmentsData(){

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
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View markerInfoWindow =
                mLayoutInflater.inflate(R.layout.locality_apartments_layout, null);

        LocalityApartmentsView localityApartmentsView = (LocalityApartmentsView) markerInfoWindow;
        localityApartmentsView.bindApartmentData();

        container.addView(markerInfoWindow);
        return markerInfoWindow;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}
