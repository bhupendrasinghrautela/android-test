package com.makaan.activity.buyerJourney;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * adapter for buyer journey
 */
public class BuyerJourneyPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public BuyerJourneyPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BuyerJourneyFragment journeyFragment = new BuyerJourneyFragment();
                return journeyFragment;
            case 1:
                BuyerJourneyFragment notificationsFragment = new BuyerJourneyFragment();
                return notificationsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}