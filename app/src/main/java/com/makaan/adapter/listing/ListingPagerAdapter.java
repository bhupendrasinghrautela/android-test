package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.listing.ListingCardView;
import com.makaan.response.listing.Listing;

import java.util.List;

/**
 *
 * Created by sunil on 03/01/16.
 *
 * Listing viewpager adapter.
 */
public class ListingPagerAdapter extends PagerAdapter {

	private Context mContext;
	private List<Listing> mProjectList;
    private int realCount;

    public ListingPagerAdapter(Context context) {
    	this.mContext = context;
    }
    
    public void setData(List<Listing> list){
    	mProjectList = list;
    	setItemCount(mProjectList.size());
    	this.notifyDataSetChanged();
    }

    private void setItemCount(int size) {
    	realCount = size;
    }

    @Override
    public int getCount() {
    	return realCount;
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
                mLayoutInflater.inflate(R.layout.listing_brief_view_layout, null);

        ListingCardView listingCardView = (ListingCardView) markerInfoWindow;
        listingCardView.bindView(mContext, mProjectList.get(position));
		container.addView(markerInfoWindow);
		return markerInfoWindow;
		
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}