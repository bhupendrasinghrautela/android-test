package com.makaan.fragment.listing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.adapter.listing.SerpListingAdapter;
import com.makaan.ui.PaginatedListView;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/6/16.
 * Listing Fragment which will house a recycler view to show all the listing as per user's search criteria
 */
public class SerpListFragment extends MakaanBaseFragment implements PaginatedListView.PaginationListener {
    private static final String KEY_IS_CHILD_SERP = "is_child_serp";
    private static final int MAX_ITEMS_TO_REQUEST = 10;

    @Bind(R.id.fragment_listing_recycler_view)
    PaginatedListView mListingRecyclerView;
    @Bind(R.id.fragment_listing_total_properties_text_view)
    TextView mTotalPropertiesTextView;

    private SerpListingAdapter mListingAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mTotalCount;
    private String mCityName;

    private boolean mIsChildSerp;

    int page;
    private SerpRequestCallback mSerpRequestCallback;
    private int mRequestType;


    private SerpGetEvent mListingGetEvent;
    private GroupSerpGetEvent mGroupListingGetEvent;
    private ArrayList<Listing> mListings = new ArrayList<Listing>();
    private ArrayList<GroupListing> mGroupListings = new ArrayList<GroupListing>();

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
        if(activity != null) {
            initializeRecyclerViewData();
            mListingRecyclerView.setPaginableListener(this);
            if(mListings != null) {
                if(mTotalPropertiesTextView != null) {
                    mTotalPropertiesTextView.setText(String.format("%d properties in %s", mTotalCount, mCityName != null ? mCityName : ""));
                }
                mListingAdapter.setData(mListings, mGroupListings, mRequestType);
                if(mTotalCount > mListings.size()) {
                    mListingRecyclerView.setHasMoreItems(true);
                } else {
                    mListingRecyclerView.setHasMoreItems(false);
                }
            } else {
                mListingRecyclerView.setHasMoreItems(false);
            }
        }
        return view;
    }

    private void initializeRecyclerViewData() {
        mListingAdapter = new SerpListingAdapter(getActivity(), mSerpRequestCallback, mRequestType);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mListingRecyclerView.setLayoutManager(mLayoutManager);
        mListingRecyclerView.setAdapter(mListingAdapter);
    }

    public void updateListings(SerpGetEvent listingGetEvent, GroupSerpGetEvent groupListingGetEvent, SerpRequestCallback serpRequestCallback, int requestType) {
        this.mSerpRequestCallback = serpRequestCallback;
        this.mRequestType = requestType;
        if(listingGetEvent == null || listingGetEvent.listingData == null) {
            if(mListingAdapter != null) {
                mListingAdapter.setData(null, mRequestType);
            }
            return;
        }
        mListingGetEvent = listingGetEvent;
        mGroupListingGetEvent = groupListingGetEvent;

        if(mListingAdapter != null) {


            mTotalCount = listingGetEvent.listingData.totalCount;
            mCityName = listingGetEvent.listingData.cityName;

            if(mTotalPropertiesTextView != null) {
                if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                    mTotalPropertiesTextView.setVisibility(View.GONE);
                } else {
                    mTotalPropertiesTextView.setVisibility(View.VISIBLE);
                    mTotalPropertiesTextView.setText(String.format("%d properties in %s", mTotalCount, mCityName != null ? mCityName : ""));
                }
            }

            if((requestType & SerpActivity.MASK_LISTING_UPDATE_TYPE) == 0) {
                mListings.clear();
            }

            mListings.addAll(listingGetEvent.listingData.listings);

            if(mGroupListingGetEvent != null && mGroupListingGetEvent.groupListingData != null) {
                mGroupListings.addAll(mGroupListingGetEvent.groupListingData.groupListings);
                mListingAdapter.setData(mListings, mGroupListings, mRequestType);
            } else {
                mGroupListings.clear();
                mListingAdapter.setData(mListings, null, mRequestType);
            }

            if(mTotalCount > mListings.size()) {
                mListingRecyclerView.setHasMoreItems(true);
            } else {
                mListingRecyclerView.setHasMoreItems(false);
            }
            mListingRecyclerView.setIsLoading(false);

            if((requestType & SerpActivity.MASK_LISTING_UPDATE_TYPE) == 0) {
                mListingRecyclerView.scrollToPosition(0);
            }
        } else {
            mTotalCount = listingGetEvent.listingData.totalCount;
            mCityName = listingGetEvent.listingData.cityName;

            mListings.clear();
            mListings.addAll(listingGetEvent.listingData.listings);
            mGroupListings.clear();
            if(mGroupListingGetEvent != null && mGroupListingGetEvent.groupListingData != null) {
                mGroupListings.addAll(mGroupListingGetEvent.groupListingData.groupListings);
            }
        }
    }

    @Override
    public void onLoadMoreItems() {
        if(mListings != null) {
            if((mListings.size() / MAX_ITEMS_TO_REQUEST) == (page + 1)) {
                page++;
                MakaanBuyerApplication.serpSelector.page(mListings.size(), MAX_ITEMS_TO_REQUEST);
                mSerpRequestCallback.serpRequest(SerpActivity.TYPE_LOAD_MORE, MakaanBuyerApplication.serpSelector);
            }
        }
    }
}
