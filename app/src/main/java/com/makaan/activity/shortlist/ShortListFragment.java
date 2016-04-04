package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.fragment.MakaanBaseFragment;
import com.segment.analytics.Properties;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListFragment extends MakaanBaseFragment {

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    enum TabType {
        Enquired("08 \nenquired"),
        Favorite("favorite"), Recent("recent");

        String value;

        TabType(String value) {
            this.value = value;
        }
    }
    @Override
    protected int getContentViewId() {
        return R.layout.layout_shortlist_activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initViews();
        return view;
    }

    private void initViews() {

        if(CookiePreferences.isUserLoggedIn(getActivity())) {
            mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Enquired.value));
        }
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Favorite.value));
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Recent.value));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ShortlistPagerAdapter adapter = new ShortlistPagerAdapter(getActivity(), getChildFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);
        if(CookiePreferences.isUserLoggedIn(getActivity())) {
            mViewPager.setCurrentItem(1);
        } else {
            mViewPager.setCurrentItem(0);
        }
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if(tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                createEventOnTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    private void createEventOnTabSelection(int position) {
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);

        switch (position){
            case 0:{
                properties.put(MakaanEventPayload.LABEL,MakaanTrackerConstants.Label.enquired);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickShortlist);
                break;
            }
            case 1:{
                properties.put(MakaanEventPayload.LABEL,MakaanTrackerConstants.Label.favourite);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickShortlist);
                break;
            }
            case 2:{
                properties.put(MakaanEventPayload.LABEL,MakaanTrackerConstants.Label.recent);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickShortlist);
                break;
            }
        }
    }


}
