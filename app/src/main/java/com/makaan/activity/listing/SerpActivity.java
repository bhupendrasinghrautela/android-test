package com.makaan.activity.listing;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.listing.ChildSerpClusterFragment;
import com.makaan.fragment.listing.FiltersDialogFragment;
import com.makaan.service.ListingService;
import com.makaan.fragment.listing.SerpListFragment;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpActivity extends MakaanBaseSearchActivity implements SerpListFragment.ListingFragmentCallbacks {
    public static boolean isChildSerp = false;

    private SerpListFragment mListingFragment;
    private SerpListFragment mChildSerpListFragment;
    private boolean wasLastChildSerp = false;

    @Bind(R.id.activity_serp_filters_frame_layout)
    FrameLayout mFiltersFrameLayout;
    @Bind(R.id.activity_serp_similar_properties_frame_layout)
    FrameLayout mSimilarPropertiesFrameLayout;
    @Bind(R.id.activity_serp_content_frame_layout)
    FrameLayout mContentFrameLayout;

    @Bind(R.id.fragment_filters_filter_relative_layout)
    RelativeLayout mFilterRelativeLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_serp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO check whether it should be used or not
        fetchData();

        // init fragments we need to use
        initUi(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isChildSerp) {
            setShowSearchBar(true);
            isChildSerp = false;
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
        }
    }

    protected void initUi(boolean showSearchBar) {
        super.initUi(true);
    }

    private void fetchData() {
        new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
    }

    @Subscribe
    public void onResults(SerpGetEvent listingGetEvent) {
        if (isChildSerp) {
            setShowSearchBar(false);
            mFiltersFrameLayout.setVisibility(View.GONE);
            mSimilarPropertiesFrameLayout.setVisibility(View.VISIBLE);
            if (mChildSerpListFragment == null || !mChildSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                // create new listing fragment to show the listings
                mChildSerpListFragment = SerpListFragment.init(true);
                mChildSerpListFragment.updateListings(listingGetEvent);

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                mChildSerpListFragment.updateListings(listingGetEvent);
            }
        } else {
            setShowSearchBar(true);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            // check if we have working listing fragment at this time
            if (mListingFragment == null) {
                // create new listing fragment to show the listings
                mListingFragment = SerpListFragment.init(false);
                mListingFragment.updateListings(listingGetEvent);
                initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            } else {
                // update already running listing fragment with the new list
                mListingFragment.updateListings(listingGetEvent);
            }
        }
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        if (isChildSerp) {
            setShowSearchBar(false);
            mFiltersFrameLayout.setVisibility(View.GONE);
            mSimilarPropertiesFrameLayout.setVisibility(View.VISIBLE);
            if (mChildSerpListFragment == null || !mChildSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                // create new listing fragment to show the listings
                mChildSerpListFragment = SerpListFragment.init(true);
                mChildSerpListFragment.updateListings(listingByIdGetEvent.listing);

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                mChildSerpListFragment.updateListings(listingByIdGetEvent.listing);
            }
        } else {
            setShowSearchBar(true);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            // check if we have working listing fragment at this time
            if (mListingFragment == null) {
                // create new listing fragment to show the listings
                mListingFragment = SerpListFragment.init(false);
                mListingFragment.updateListings(listingByIdGetEvent.listing);
                initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            } else {
                // update already running listing fragment with the new list
                mListingFragment.updateListings(listingByIdGetEvent.listing);
            }
        }
    }

    @Override
    public SerpGetEvent getListings(int flag) {
        return null;
    }
    @OnClick(R.id.fragment_filters_filter_relative_layout)
    public void onFilterPressed(View view) {
        if(view == mFilterRelativeLayout) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            FiltersDialogFragment filterFragment = FiltersDialogFragment.init();
            filterFragment.show(ft, "Filters");
        }
    }

    public interface ActivityCallbacks {
        void updateListings(SerpGetEvent listingGetEvent);
    }
}
