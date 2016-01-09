package com.makaan.activity.serp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.response.listing.Listing;
import com.makaan.ui.adapter.ListingAdapter;
import com.makaan.ui.interfaces.ListingActivityCallbacks;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/6/16.
 * Listing Fragment which will house a recycler view to show all the listing as per user's search criteria
 */
public class SerpListFragment extends Fragment implements ListingActivityCallbacks,
        ListingAdapter.ListingAdapterCallbacks {

    @Bind(R.id.fragment_listing_recycler_view)
    RecyclerView mListingRecyclerView;
    private ListingAdapter mListingAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Listing> mListings;

    public static SerpListFragment init() {
        // create listing fragment to show listing of the request we are receiving
        return new SerpListFragment();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listing, container, false);
        // bind view to ButterKnife
        ButterKnife.bind(this, view);

        // get listing from parent activity
        Activity activity = getActivity();
        if(activity != null && activity instanceof ListingFragmentCallbacks) {
            // update that there is one working fragment available for normal listing
            ((ListingFragmentCallbacks)activity).updateListingFragment(this);
            initializeRecyclerViewData();
            mListingAdapter.setData(mListings);
        }
        return view;
    }

    private void initializeRecyclerViewData() {
        mListingAdapter = new ListingAdapter(getActivity(), this);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mListingRecyclerView.setLayoutManager(mLayoutManager);
        mListingRecyclerView.setAdapter(mListingAdapter);
    }

    @Override
    public void updateListings(SerpGetEvent listingGetEvent) {
        this.mListings = listingGetEvent.listingData.listings;
    }

    @Override
    public void updateListings(Listing listing) {
        ArrayList<Listing> listings = new ArrayList<>();
        listings.add(listing);
        this.mListings = listings;
    }

    /***
     * this interface will be implemented by Housing/Parent activity
     * which will receive communications/callbacks from this fragment
     */
    public interface ListingFragmentCallbacks {
        /**
         * update SerpListFragment to the housing/parent activity so that activity won't create new one
         * @param listingFragmentCallbacks
         */
        void updateListingFragment(ListingActivityCallbacks listingFragmentCallbacks);

        /**
         * get listing from the housing/parent activity
         * @param flag request flag to make sure fragment is requesting correct data
         * @return Listings
         */
        SerpGetEvent getListings(int flag);
    }
}
