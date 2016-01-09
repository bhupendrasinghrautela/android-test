package com.makaan.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.view.TempClusterItemView;

import java.util.List;

import pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class HorizontalScrollAdapter extends PagerAdapter {
    private final Context context;
    private final List<Object> listing;

    public HorizontalScrollAdapter(Context context, List<Object> listing) {
        this.context = context;
        this.listing = listing;
    }

    @Override
    public int getCount() {
        if(listing != null) {
            return listing.size();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        if(listing != null && listing.get(position) instanceof TempClusterItem) {
            View view = LayoutInflater.from(context).inflate(R.layout.cluster_item_view, null);
            ((TempClusterItemView)view).populateView((TempClusterItem)listing.get(position));
            collection.addView(view);
            return view;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
