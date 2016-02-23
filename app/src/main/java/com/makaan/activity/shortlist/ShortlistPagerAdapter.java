package com.makaan.activity.shortlist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter for buyer journey
 */
public class ShortlistPagerAdapter extends FragmentStatePagerAdapter implements ShortListCallback {
    int mNumOfTabs;
    Context mContext;
    String [] label = {"enquired","favorite","recent"};
    String [] count = {"00","00","00"};
    List<View> mViews;

    public ShortlistPagerAdapter(Context context , FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.mContext=context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ShortlistEnquiredFragment enquiredFragment = new ShortlistEnquiredFragment();
                enquiredFragment.bindView(this,0);
                return enquiredFragment;
            case 1:
                ShortListFavoriteFragment favoriteFragment = new ShortListFavoriteFragment();
                favoriteFragment.bindView(this, 1);
                return favoriteFragment;
            case 2:
                //TODO change this to recent
                ShortListRecentFragment recentFragment = new ShortListRecentFragment();
                recentFragment.bindView(this, 2);
                return recentFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        if(mViews == null){
            mViews = new ArrayList<>();
        }
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        TextView countValue = (TextView) v.findViewById(R.id.tv_count);
        TextView type = (TextView) v.findViewById(R.id.tv_type);
        type.setText(label[position]);
        countValue.setText(count[position]);
        mViews.add(v);
        return v;
    }


    @Override
    public void updateCount(int position,int count) {
        View v = mViews.get(position);
        TextView countValue = (TextView) v.findViewById(R.id.tv_count);
        if(count < 10) {
            countValue.setText(String.format("%02d", count));
        } else {
            countValue.setText(String.valueOf(count));
        }

    }
}