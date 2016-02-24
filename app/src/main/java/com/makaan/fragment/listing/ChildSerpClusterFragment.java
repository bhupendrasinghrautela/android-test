package com.makaan.fragment.listing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.HorizontalScrollFragmentAdapter;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.GroupCluster;
import com.makaan.response.listing.GroupListing;
import com.makaan.ui.view.CustomViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/11/16.
 */
public class ChildSerpClusterFragment extends MakaanBaseFragment {
    @Bind(R.id.fragment_child_serp_cluster_back_button)
    Button mBackButton;

    @Bind(R.id.fragment_child_serp_cluster_view_pager)
    CustomViewPager mViewPager;
    @Bind(R.id.fragment_child_serp_cluster_left_arrow_image_view)
    ImageView mLeftArrowImageView;
    @Bind(R.id.fragment_child_serp_cluster_right_arrow_image_view)
    ImageView mRightArrowImageView;

    private HorizontalScrollFragmentAdapter mFragmentPagerAdapter;
    private SerpRequestCallback mCallback;
    private GroupCluster mCluster;
    private Long mSelectedChildListingId;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_child_serp_cluster;
    }

    public static ChildSerpClusterFragment init() {
        return new ChildSerpClusterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if(mCluster != null) {
            mFragmentPagerAdapter = new HorizontalScrollFragmentAdapter<>(getActivity().getSupportFragmentManager(), getActivity(), mCluster.groupListings, true, mCallback);

            mViewPager.setAdapter(mFragmentPagerAdapter);

            if(mSelectedChildListingId != null && mSelectedChildListingId > 0) {
                for(int i = 0; i < mCluster.groupListings.size(); i++) {
                    if(mCluster.groupListings.get(i).listing.id.equals(mSelectedChildListingId)) {
                        mViewPager.setCurrentItem(i);
                        break;
                    }
                }
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    invalidateArrowButtons();
                }

                private void invalidateArrowButtons() {
                    if (!mViewPager.canScrollHorizontally(1)) {
                        mRightArrowImageView.setVisibility(View.INVISIBLE);
                    } else {
                        mRightArrowImageView.setVisibility(View.VISIBLE);
                    }
                    if (!mViewPager.canScrollHorizontally(-1)) {
                        mLeftArrowImageView.setVisibility(View.INVISIBLE);
                    } else {
                        mLeftArrowImageView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    if (mCallback != null && mCluster != null && mCluster.groupListings.size() > position) {
                        mCallback.serpRequest(SerpActivity.TYPE_CLUSTER, mCluster.groupListings.get(position).listing.id);
                    }

                    invalidateArrowButtons();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            mLeftArrowImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                }
            });
            mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            });
        }
        return view;
    }

    @OnClick(R.id.fragment_child_serp_cluster_back_button)
    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }

    public void setData(ArrayList<GroupListing> groupListings, Long childSerpId, SerpRequestCallback callback, Long childListingId) {
        mCallback = callback;
        mSelectedChildListingId = childListingId;
        if(groupListings != null) {
            mCluster = GroupCluster.getGroupCluster(groupListings, childSerpId);
        }
    }
}
