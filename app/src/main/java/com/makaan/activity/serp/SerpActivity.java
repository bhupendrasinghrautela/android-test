package com.makaan.activity.serp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.listing.ListingService;
import com.makaan.ui.fragment.FiltersFragment;
import com.makaan.activity.serp.SerpListFragment;
import com.makaan.ui.fragment.SearchBarFragment;
import com.makaan.ui.fragment.SearchResultsFragment;
import com.makaan.ui.interfaces.ListingActivityCallbacks;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpActivity extends MakaanFragmentActivity implements SerpListFragment.ListingFragmentCallbacks,
        FiltersFragment.FiltersFragmentCallbacks, SearchBarFragment.SearchBarFragmentCallbacks {
    @Bind(R.id.search_results)
    FrameLayout mSearchLayout;

    private FragmentTransaction mFragmentTransaction;
    private SerpGetEvent listingGetEvent;
    private ListingActivityCallbacks listingFragment;
    private FiltersFragment filtersFragment;
    private ListingByIdGetEvent listingByIdGetEvent;
    private SearchResultsFragment mSearchResultsFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.content_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        // TODO check whether it should be used or not
        fetchData();
    }


    @OnClick(R.id.fetch)
    public void fetch(View view) {
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyTypes();
        new ListingService().getListingDetail(1L);
    }

    private void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if(shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    private void fetchData() {
        new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
    }

    @Subscribe
    public void onResults(SerpGetEvent listingGetEvent) {
        this.listingGetEvent = listingGetEvent;
        // check if we have working listing fragment at this time
        if (listingFragment == null) {

            // new fragment to show search bar top of listing
            SearchBarFragment searchBarFragment = SearchBarFragment.init(SearchBarFragment.TYPE_SEARCH);
            initFragment(R.id.top_bar, searchBarFragment, false);

            // new fragment to show search bar top of listing
            SearchResultsFragment searchResultsFragment = SearchResultsFragment.init();
            initFragment(R.id.search_results, searchResultsFragment, false);
            mSearchResultsFragment = searchResultsFragment;

            // new fragment to show filters
            FiltersFragment filtersFragment = FiltersFragment.init();
            initFragment(R.id.filters, filtersFragment, false);

            // create new listing fragment to show the listings
            SerpListFragment listingFragment = SerpListFragment.init();
            listingFragment.updateListings(listingGetEvent);
            initFragment(R.id.content, listingFragment, false);
        } else {
            // update already running listing fragment with the new list
            listingFragment.updateListings(listingGetEvent);
        }
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent){
        this.listingByIdGetEvent = listingByIdGetEvent;
        // check if we have working listing fragment at this time
        if(listingFragment == null) {

            // new fragment to show search bar top of listing
            SearchBarFragment searchBarFragment = SearchBarFragment.init(SearchBarFragment.TYPE_SEARCH);
            initFragment(R.id.top_bar, searchBarFragment, false);

            // new fragment to show search bar top of listing
            SearchResultsFragment searchResultsFragment = SearchResultsFragment.init();
            initFragment(R.id.search_results, searchResultsFragment, false);
            mSearchResultsFragment = searchResultsFragment;

            // new fragment to show filters on top of listing
            FiltersFragment filtersFragment = FiltersFragment.init();
            initFragment(R.id.filters, filtersFragment, false);

            // create new listing fragment to show the listings
            SerpListFragment listingFragment = SerpListFragment.init();
            listingFragment.updateListings(listingByIdGetEvent.listing);
        } else {
            // update already running listing fragment with the new list
            listingFragment.updateListings(listingByIdGetEvent.listing);
        }
    }

    @Override
    public SerpGetEvent getListings(int flag) {
        return null;
    }

    @Override
    public void updateListingFragment(ListingActivityCallbacks listingFragmentCallbacks) {
        this.listingFragment = listingFragmentCallbacks;
    }

    @Override
    public void updateFiltersFragment(FiltersFragment filtersFragment) {
        this.filtersFragment = filtersFragment;
    }

    @Override
    public void onQueryChanged(String query) {
        if(query != null) {
            mSearchLayout.setVisibility(View.VISIBLE);
            if(mSearchResultsFragment != null && mSearchResultsFragment instanceof SearchBarFragment.SearchBarFragmentCallbacks) {
                ((SearchBarFragment.SearchBarFragmentCallbacks)mSearchResultsFragment).onQueryChanged(query);
            }
        }
    }

    public interface ActivityCallbacks {
        void updateListings(SerpGetEvent listingGetEvent);
    }
}
