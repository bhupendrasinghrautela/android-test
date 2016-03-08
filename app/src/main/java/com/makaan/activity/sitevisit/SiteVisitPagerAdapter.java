package com.makaan.activity.sitevisit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.activity.shortlist.ShortListCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter for buyer journey
 */
public class SiteVisitPagerAdapter extends FragmentStatePagerAdapter implements ShortListCallback {
    private BuyerDashboardCallbacks mCallbacks;
    int mNumOfTabs;
    Context mContext;
    String [] label = {"site visits"};
    String [] count = {"00"};
    List<View> mViews;

    public SiteVisitPagerAdapter(Context context, FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
        this.mContext=context;
        if(context instanceof BuyerDashboardCallbacks) {
            this.mCallbacks = (BuyerDashboardCallbacks)context;
        }
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SiteVisitUpcommingFragment enquiredFragment = new SiteVisitUpcommingFragment();
                enquiredFragment.bindView(this,0, mCallbacks);
                return enquiredFragment;
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
        countValue.setText(String.valueOf(count));
    }
}