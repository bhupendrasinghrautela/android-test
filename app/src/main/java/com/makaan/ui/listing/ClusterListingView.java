package com.makaan.ui.listing;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.HorizontalScrollFragmentAdapter;
import com.makaan.pojo.GroupCluster;
import com.makaan.response.listing.GroupListing;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class ClusterListingView extends AbstractCardListingView {

    @Bind(R.id.cluster_view_pager)
    ViewPager mViewPager;

    @Bind(R.id.cluster_view_left_button)
    Button mLeftArrowImageButton;
    @Bind(R.id.cluster_view_right_button)
    Button mRightArrowImageButton;

    private PagerAdapter viewPagerAdapter;
    private FragmentStatePagerAdapter fragmentPagerAdapter;
    private ArrayList<GroupListing> mListing;

    public ClusterListingView(Context context) {
        super(context);
    }

    public ClusterListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClusterListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);
        if(data != null && data instanceof GroupCluster) {
            fragmentPagerAdapter = new HorizontalScrollFragmentAdapter<GroupListing>(
                    ((FragmentActivity) mContext).getSupportFragmentManager(), mContext,
                    ((GroupCluster) data).groupListings, false, callback);
            mViewPager.setAdapter(fragmentPagerAdapter);
            Long lastUsedClusterId = callback.getLastUsedClusterId();
            if(lastUsedClusterId != null) {
                for (int i = 0; i < ((GroupCluster) data).groupListings.size(); i++) {
                    if (((GroupCluster) data).groupListings.get(i).listing.id.equals(lastUsedClusterId)) {
                        mViewPager.setCurrentItem(i);
                    }
                }
            }
        }
        /*if(data!= null && data instanceof ArrayList<?>) {
            if(((ArrayList<?>)data).size() > 0 && ((ArrayList<?>)data).get(0) instanceof GroupCluster) {
                mListing = (ArrayList<GroupListing>) data;
                if (mContext instanceof FragmentActivity) {
                    fragmentPagerAdapter = new HorizontalScrollFragmentAdapter<GroupCluster>(
                            ((FragmentActivity) mContext).getSupportFragmentManager(), mContext,
                            (List<GroupCluster>) data, false, callback);

                    viewPager.setAdapter(fragmentPagerAdapter);
                }
            }
        }*/
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                invalidateArrowButtons();
            }

            private void invalidateArrowButtons() {
                if (!mViewPager.canScrollHorizontally(1)) {
                    mRightArrowImageButton.setVisibility(View.INVISIBLE);
                } else {
                    mRightArrowImageButton.setVisibility(View.VISIBLE);
                }
                if (!mViewPager.canScrollHorizontally(-1)) {
                    mLeftArrowImageButton.setVisibility(View.INVISIBLE);
                } else {
                    mLeftArrowImageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                invalidateArrowButtons();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.cluster_view_left_button, R.id.cluster_view_right_button})
    public void onPageChangePressed(View view) {
        switch (view.getId()) {
            case R.id.cluster_view_left_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                break;
            case R.id.cluster_view_right_button:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                break;
        }
    }
}
