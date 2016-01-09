package com.makaan.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.fragment.ScreenSlidePageFragment;
import com.makaan.ui.view.TempClusterItemView;

import java.util.List;

import pojo.TempClusterItem;

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
