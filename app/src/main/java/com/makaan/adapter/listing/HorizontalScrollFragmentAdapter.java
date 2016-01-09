package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.makaan.fragment.listing.ScreenSlidePageFragment;

import java.util.List;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class HorizontalScrollFragmentAdapter extends FragmentStatePagerAdapter {
    private final Context context;
    private final List<Object>  listing;

    public HorizontalScrollFragmentAdapter(FragmentManager fm, Context context, List<Object> data) {
        super(fm);
        this.context = context;
        this.listing = data;
    }

    @Override
    public Fragment getItem(int position) {
        if(listing != null && listing.size() > position) {
            ScreenSlidePageFragment fragment = ScreenSlidePageFragment.init(position);
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
