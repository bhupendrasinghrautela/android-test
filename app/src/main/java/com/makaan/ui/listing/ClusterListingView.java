package com.makaan.ui.listing;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.HorizontalScrollFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import com.makaan.pojo.GroupCluster;
import com.makaan.response.listing.GroupListing;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class ClusterListingView extends AbstractCardListingView {

    @Bind(R.id.cluster_view_pager)
    ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private FragmentStatePagerAdapter fragmentPagerAdapter;
    private ArrayList<GroupListing> mListing;

    public ClusterListingView(Context context) {
        super(context);
    }

    public ClusterListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClusterListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);
        if(data != null && data instanceof GroupCluster) {
            fragmentPagerAdapter = new HorizontalScrollFragmentAdapter<GroupListing>(
                    ((FragmentActivity) mContext).getSupportFragmentManager(), mContext,
                    (List<GroupListing>) (((GroupCluster) data).groupListings), false, callback);
            viewPager.setAdapter(fragmentPagerAdapter);
        }
        /*if(data!= null && data instanceof ArrayList<?>) {
            if(((ArrayList<?>)data).size() > 0 && ((ArrayList<?>)data).get(0) instanceof GroupCluster) {
                mListing = (ArrayList<GroupListing>) data;
                if (mContext instanceof FragmentActivity) {
                    fragmentPagerAdapter = new HorizontalScrollFragmentAdapter<GroupCluster>(
                            ((FragmentActivity) mContext).getSupportFragmentManager(), mContext,
                            (List<GroupCluster>) data, false, callback);

                    viewPager.setAdapter(fragmentPagerAdapter);
                }
            }
        }*/
    }
}
