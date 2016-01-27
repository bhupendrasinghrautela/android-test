package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.fragment.listing.HorizontalScrollPageFragment;

import java.util.List;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class HorizontalScrollFragmentAdapter<T> extends FragmentStatePagerAdapter {
    private final Context mContext;
    private final List<T> mListing;
    private final SerpRequestCallback mCallback;
    private boolean mIsChildSerpClusterAdapter;

    public HorizontalScrollFragmentAdapter(FragmentManager fm, Context context, List<T> data,
                                           boolean isChildSerpClusterView, SerpRequestCallback callback) {
        super(fm);
        this.mContext = context;
        this.mListing = data;
        this.mIsChildSerpClusterAdapter = isChildSerpClusterView;
        this.mCallback = callback;
    }

    @Override
    public Fragment getItem(int position) {
        if(mListing != null && mListing.size() > position) {
            HorizontalScrollPageFragment fragment = HorizontalScrollPageFragment.init(mIsChildSerpClusterAdapter);
            fragment.populateData(mListing.get(position), mCallback);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if(mListing != null) {
            return mListing.size();
        }
        return 0;
    }
}
