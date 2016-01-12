package com.makaan.fragment.listing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.Listing;
import com.makaan.adapter.listing.SerpListingAdapter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/6/16.
 * Listing Fragment which will house a recycler view to show all the listing as per user's search criteria
 */
public class SerpListFragment extends MakaanBaseFragment implements SerpListingAdapter.ListingAdapterCallbacks {
    private static final String KEY_IS_CHILD_SERP = "is_child_serp";

    @Bind(R.id.fragment_listing_recycler_view)
    RecyclerView mListingRecyclerView;
    @Bind(R.id.fragment_listing_total_properties_text_view)
    TextView mTotalPropertiesTextView;

    private SerpListingAdapter mListingAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Listing> mListings;
    private boolean mIsChildSerp;

    public static SerpListFragment init(boolean isChildSerp) {
        // create listing fragment to show listing of the request we are receiving
        SerpListFragment fragment = new SerpListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CHILD_SERP, isChildSerp);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_listing;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(KEY_IS_CHILD_SERP)) {
            mIsChildSerp = bundle.getBoolean(KEY_IS_CHILD_SERP);
        }

        // get listing from parent activity
        Activity activity = getActivity();
        if(activity != null && activity instanceof ListingFragmentCallbacks) {
            initializeRecyclerViewData();
            if(mListings != null) {
                mTotalPropertiesTextView.setText(String.format(Locale.ENGLISH, "%d properties", mListings.size()));
                mListingAdapter.setData(mListings);
            }
        }
        return view;
    }

    private void initializeRecyclerViewData() {
        mListingAdapter = new SerpListingAdapter(getActivity(), this, mIsChildSerp);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mListingRecyclerView.setLayoutManager(mLayoutManager);
        mListingRecyclerView.setAdapter(mListingAdapter);
    }

    public void updateListings(SerpGetEvent listingGetEvent) {
        this.mListings = listingGetEvent.listingData.listings;
        if(mListings == null) {
            mListings = new ArrayList<Listing>();
        }
        if(mListingAdapter != null) {
            mTotalPropertiesTextView.setText(String.format(Locale.ENGLISH, "%d properties", mListings.size()));
            mListingAdapter.setData(mListings);
        }
    }

    public void updateListings(Listing listing) {
        ArrayList<Listing> listings = new ArrayList<>();
        if(listing != null) {
            listings.add(listing);
        }
        this.mListings = listings;
        if(mListingAdapter != null) {
            mTotalPropertiesTextView.setText(String.format(Locale.ENGLISH, "%d properties", mListings.size()));
            mListingAdapter.setData(mListings);
        }
    }

    /***
     * this interface will be implemented by Housing/Parent activity
     * which will receive communications/callbacks from this fragment
     */
    public interface ListingFragmentCallbacks {

        /**
         * get listing from the housing/parent activity
         * @param flag request flag to make sure fragment is requesting correct data
         * @return Listings
         */
        SerpGetEvent getListings(int flag);
    }
}
