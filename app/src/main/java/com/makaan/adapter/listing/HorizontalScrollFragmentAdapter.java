package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.makaan.fragment.listing.HorizontalScrollPageFragment;

import java.util.List;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class HorizontalScrollFragmentAdapter<T> extends FragmentStatePagerAdapter {
    private final Context context;
    private final List<T>  listing;
    private boolean isChildSerpClusterAdapter;

    public HorizontalScrollFragmentAdapter(FragmentManager fm, Context context, List<T> data, boolean isChildSerpClusterView) {
        super(fm);
        this.context = context;
        this.listing = data;
        this.isChildSerpClusterAdapter = isChildSerpClusterView;
    }

    @Override
    public Fragment getItem(int position) {
        if(listing != null && listing.size() > position) {
            HorizontalScrollPageFragment fragment = HorizontalScrollPageFragment.init(isChildSerpClusterAdapter);
            fragment.populateData(listing.get(position));
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if(listing != null) {
            return listing.size();
        }
        return 0;
    }
}
