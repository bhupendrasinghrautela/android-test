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
import com.makaan.ui.listing.LoadMoreListingCardView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by sunil on 03/01/16.
 *
 * Listing viewpager adapter.
 */
public class ListingPagerAdapter extends PagerAdapter {

    private final PaginationListener mListener;

    public interface PaginationListener {
        void onLoadMoreItems();
    }

	private Context mContext;
	private List<Listing> mProjectList = new ArrayList<>();
    private int realCount;
    boolean mNeedMoreItem = false;

    public ListingPagerAdapter(Context context, PaginationListener listener) {
    	this.mContext = context;
        mListener = listener;
    }
    
    public void setData(List<Listing> list, boolean needLoadMore){
        mProjectList.clear();
    	mProjectList.addAll(list);
        mNeedMoreItem = needLoadMore;
    	setItemCount(needLoadMore ? mProjectList.size() + 1 : mProjectList.size());
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
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if(mNeedMoreItem && position == getCount() - 1) {
            // TODO add load more item
            LayoutInflater mLayoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View view =
                    mLayoutInflater.inflate(R.layout.listing_load_more_view_layout, null);

//            LoadMoreListingCardView listingCardView = (LoadMoreListingCardView) view;
//            listingCardView.bindView(mContext, mListener);
            container.addView(view);
            return view;
        } else {
            LayoutInflater mLayoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View markerInfoWindow =
                    mLayoutInflater.inflate(R.layout.listing_brief_view_layout, null);

            ListingCardView listingCardView = (ListingCardView) markerInfoWindow;
            listingCardView.populateData(mProjectList.get(position), null, position);
            container.addView(markerInfoWindow);
            return markerInfoWindow;
        }
		
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}