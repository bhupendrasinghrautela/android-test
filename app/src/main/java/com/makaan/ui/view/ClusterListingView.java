package com.makaan.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.HorizontalScrollFragmentAdapter;

import java.util.List;

import butterknife.Bind;
import com.makaan.pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class ClusterListingView extends AbstractListingView {
    /*@Bind(R.id.cluster_view_left_arrow)
    ImageView leftArrowImageView;
    @Bind(R.id.cluster_view_right_arrow)
    ImageView rightArrowImageView;*/
    @Bind(R.id.cluster_view_pager)
    ViewPager viewPager;
    private PagerAdapter viewPagerAdapter;
    private FragmentStatePagerAdapter fragmentPagerAdapter;

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
        if(data != null && data instanceof List && ((List) data).size() > 0 && ((List) data).get(0) instanceof TempClusterItem) {
            if(mContext instanceof FragmentActivity) {
                fragmentPagerAdapter = new HorizontalScrollFragmentAdapter<TempClusterItem>(
                        ((FragmentActivity)mContext).getSupportFragmentManager(), mContext,
                        (List<TempClusterItem>) data, false, callback);
                viewPager.setAdapter(fragmentPagerAdapter);
            }

            /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    invalidateArrowButtons();
                }

                private void invalidateArrowButtons() {
                    if (!viewPager.canScrollHorizontally(1)) {
                        rightArrowImageView.setVisibility(View.GONE);
                    } else {
                        rightArrowImageView.setVisibility(View.VISIBLE);
                    }
                    if (!viewPager.canScrollHorizontally(-1)) {
                        leftArrowImageView.setVisibility(View.GONE);
                    } else {
                        leftArrowImageView.setVisibility(View.VISIBLE);
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

            leftArrowImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                }
            });
            rightArrowImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }
            });*/
        }
    }
}
