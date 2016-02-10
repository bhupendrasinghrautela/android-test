package com.makaan.ui.listing;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.ListingPagerAdapter;
import com.makaan.response.listing.Listing;
import com.makaan.util.AnimUtil;

import java.util.List;

/**
 * Created by sunil on 06/01/16.
 */
public class ListingViewPager extends ViewPager implements ListingPagerAdapter.PaginationListener {
    private static final int MARGIN = 30;
    private static final int H_PADDING = 120;
    private Context mContext;
    ListingPagerAdapter mListingPagerAdapter;
    private SerpRequestCallback mCallback;

    public ListingViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public ListingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void bindView(){
        mListingPagerAdapter = new ListingPagerAdapter(mContext, this);
        setAdapter(mListingPagerAdapter);
        setClipToPadding(false);
        setPageMargin(mContext.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_gap));
        setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_left_padding), 0,
                mContext.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_right_padding),
                mContext.getResources().getDimensionPixelSize(R.dimen.map_listing_view_pager_page_bottom_padding));
        setOffscreenPageLimit(2);
    }

    public void setData(List<Listing> listings, boolean needLoadMore, SerpRequestCallback callback){
        mCallback = callback;
        mListingPagerAdapter.setData(listings, needLoadMore);
    }

    public void hide(){
        if(getVisibility()== View.VISIBLE){
            Animation anim = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.fad_out_move_up);
            setVisibility(View.GONE);
            startAnimation(anim);
            AnimUtil.clearAnimationAfterFinish((Activity) mContext, anim, this);
        }
    }

    public void show(){
        if(getVisibility()!=View.VISIBLE){
            Animation anim = AnimationUtils.loadAnimation(mContext.getApplicationContext(),
                    R.anim.fad_in_move_down);
            setVisibility(View.VISIBLE);
            startAnimation(anim);
            AnimUtil.clearAnimationAfterFinish((Activity) mContext, anim, this);
        }
    }

    @Override
    public void onLoadMoreItems() {
        if(mCallback != null) {
            mCallback.loadMoreItems();
        }
    }
}
