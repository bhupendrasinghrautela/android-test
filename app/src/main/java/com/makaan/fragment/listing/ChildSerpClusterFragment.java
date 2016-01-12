package com.makaan.fragment.listing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.listing.HorizontalScrollFragmentAdapter;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.TempClusterItem;
import com.makaan.ui.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/11/16.
 */
public class ChildSerpClusterFragment extends MakaanBaseFragment {
    private static ArrayList<TempClusterItem> mItems;

    @Bind(R.id.fragment_child_serp_cluster_back_button)
    Button mBackButton;

    @Bind(R.id.fragment_child_serp_cluster_info_text_view)
    TextView mClusterInfoTextView;

    @Bind(R.id.fragment_child_serp_cluster_map_image_view)
    ImageView mMapImageView;

    @Bind(R.id.fragment_child_serp_cluster_view_pager)
    CustomViewPager mViewPager;
    @Bind(R.id.fragment_child_serp_cluster_left_arrow_image_view)
    ImageView mLeftArrowImageView;
    @Bind(R.id.fragment_child_serp_cluster_right_arrow_image_view)
    ImageView mRightArrowImageView;

    private HorizontalScrollFragmentAdapter mFragmentPagerAdapter;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_child_serp_cluster;
    }

    public static ChildSerpClusterFragment init() {
        mItems = new ArrayList<>();
        mItems.add(new TempClusterItem("\u20B9 40l to \u20B945l", "2 bhk apartment", "sector 2, sohna road", "view 100 listings"));
        mItems.add(new TempClusterItem("\u20B9 50l to \u20B955l", "3 bhk apartment", "sector 3, sohna road", "view 200 listings"));
        mItems.add(new TempClusterItem("\u20B9 60l to \u20B965l", "3 bhk apartment", "sector 4, sohna road", "view 300 listings"));
        mItems.add(new TempClusterItem("\u20B9 70l to \u20B975l", "4 bhk apartment", "sector 5, sohna road", "view 400 listings"));

        return new ChildSerpClusterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        /*// get listing from parent activity
        Activity activity = getActivity();
        if(activity != null && activity instanceof ListingFragmentCallbacks) {
            initializeRecyclerViewData();
            if(mListings != null) {
                mListingAdapter.setData(mListings);
            }
        }*/
        mFragmentPagerAdapter = new HorizontalScrollFragmentAdapter<>(getActivity().getSupportFragmentManager(), getActivity(), mItems, true);

        mViewPager.setAdapter(mFragmentPagerAdapter);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                invalidateArrowButtons();
            }

            private void invalidateArrowButtons() {
                if (!mViewPager.canScrollHorizontally(1)) {
                    mRightArrowImageView.setVisibility(View.GONE);
                } else {
                    mRightArrowImageView.setVisibility(View.VISIBLE);
                }
                if (!mViewPager.canScrollHorizontally(-1)) {
                    mLeftArrowImageView.setVisibility(View.GONE);
                } else {
                    mLeftArrowImageView.setVisibility(View.VISIBLE);
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
        return view;
    }

    @OnClick(R.id.fragment_child_serp_cluster_back_button)
    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }
}
