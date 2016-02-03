package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.buyerJourney.BuyerJourneyPagerAdapter;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.response.wishlist.WishListResultCallback;
import com.makaan.service.LeadInstantCallbackService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListActivity extends MakaanFragmentActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    private void initViews() {

        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Enquired.value));
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Favorite.value));
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Recent.value));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ShortlistPagerAdapter adapter = new ShortlistPagerAdapter(this,getSupportFragmentManager(), mTabLayout.getTabCount());

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
