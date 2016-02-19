package com.makaan.activity.sitevisit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/02/16.
 */
public class SiteVisitFragment extends MakaanBaseFragment {
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    enum TabType {
        SiteVisit("08 \nsite visits");

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

        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.SiteVisit.value));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final SiteVisitPagerAdapter adapter = new SiteVisitPagerAdapter(getActivity(), getChildFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
