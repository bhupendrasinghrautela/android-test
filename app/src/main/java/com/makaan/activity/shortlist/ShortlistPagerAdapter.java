package com.makaan.activity.shortlist;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerJourneyFragment;

/**
 * adapter for buyer journey
 */
public class ShortlistPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context mContext;
    String [] label = {"enquired","favorite","recent"};
    String [] count = {"00","08","00"};

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
                return enquiredFragment;
            case 1:
                ShortListFavoriteFragment favoriteFragment = new ShortListFavoriteFragment();
                return favoriteFragment;
            case 2:
                //TODO change this to recent
                ShortListRecentFragment recentFragment = new ShortListRecentFragment();
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        TextView countValue = (TextView) v.findViewById(R.id.tv_count);
        TextView type = (TextView) v.findViewById(R.id.tv_type);
        type.setText(label[position]);
        countValue.setText(count[position]);

        return v;
    }


}