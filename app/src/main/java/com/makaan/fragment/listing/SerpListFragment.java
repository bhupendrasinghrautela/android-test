package com.makaan.fragment.listing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.fragment.MakaanBaseFragment;

import com.makaan.jarvis.JarvisConstants;
import com.makaan.pojo.GroupCluster;
import com.makaan.request.selector.Selector;

import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.adapter.listing.SerpListingAdapter;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.ui.PaginatedListView;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/6/16.
 * Listing Fragment which will house a recycler view to show all the listing as per user's search criteria
 */
public class SerpListFragment extends MakaanBaseFragment implements PaginatedListView.PaginationListener, PaginatedListView.ScrollListener {
    private static final String KEY_IS_CHILD_SERP = "is_child_serp";

    @Bind(R.id.fragment_listing_recycler_view)
    PaginatedListView mListingRecyclerView;
    /*@Bind(R.id.fragment_listing_total_properties_text_view)
    TextView mTotalPropertiesTextView;*/

    private SerpListingAdapter mListingAdapter;
    private LinearLayoutManager mLayoutManager;
    private String mSearchedEntities = "";

    private boolean mIsChildSerp;
    private boolean mIsScrollEventSent;

    int page;
    private SerpRequestCallback mSerpRequestCallback;
    private int mRequestType;

    private ArrayList<Listing> mListings = new ArrayList<Listing>();
    private ArrayList<GroupListing> mGroupListings = new ArrayList<GroupListing>();
    private int mTotalCount;

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
//            mListingRecyclerView.setScrollListener(this);
            if(mListings != null) {
                /*if(mTotalPropertiesTextView != null) {
                    mTotalPropertiesTextView.setText(String.format("%d %s", mTotalCount, mSearchedEntities));
                }*/
                mListingAdapter.setData(String.format("%d %s", mTotalCount, mSearchedEntities), mListings, mGroupListings, mRequestType);
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

    public void updateListings(ArrayList<Listing> listings, ArrayList<GroupListing> groupListings,
                               ArrayList<SearchResponseItem> selectedSearches, SerpRequestCallback serpRequestCallback,
                               int requestType, int listingTotalCount) {
        this.mSerpRequestCallback = serpRequestCallback;
        this.mRequestType = requestType;
        if(groupListings == null && listings == null) {
            if(mListingAdapter != null) {
                mListingAdapter.setData(null, mRequestType);
            }
            return;
        }
        mTotalCount = listingTotalCount;

        StringBuilder builder = new StringBuilder();
        builder.append(" properties ");

        if(selectedSearches.size() > 0 && SearchSuggestionType.GOOGLE_PLACE.getValue()
                .equalsIgnoreCase(selectedSearches.get(0).type)) {
            builder.append("near ");
        } else if(selectedSearches.size() > 0) {
            builder.append("in ");
        }

        if(selectedSearches != null && selectedSearches.size() > 0) {
            if(SearchSuggestionType.CITY.getValue().equals(selectedSearches.get(0).type)) {
                if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                    builder.append(selectedSearches.get(0).city);
                }
            } else {
                for(SearchResponseItem search : selectedSearches) {
                    if(!TextUtils.isEmpty(search.entityName)) {
                        if(SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(search.type)) {
                            builder.append(search.entityName);
                        } else {
                            builder.append(String.format("%s, ", search.entityName));
                        }
                    }
                }
                if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                    builder.append(selectedSearches.get(0).city);
                }
            }
        }
        mSearchedEntities = builder.toString();

        if(mListingAdapter != null && mListingRecyclerView != null) {

            /*if(mTotalPropertiesTextView != null) {
                if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                    mTotalPropertiesTextView.setVisibility(View.GONE);
                } else {
                    mTotalPropertiesTextView.setVisibility(View.VISIBLE);
                    mTotalPropertiesTextView.setText(String.format("%d %s", listingTotalCount, mSearchedEntities));
                }
            }*/

            if((requestType & SerpActivity.MASK_LISTING_UPDATE_TYPE) != SerpActivity.TYPE_LOAD_MORE) {
                mListingRecyclerView.scrollToPosition(0);
            }
            mListings.clear();
            mGroupListings.clear();

            mListings.addAll(listings);

            if(groupListings != null) {
                mGroupListings.addAll(groupListings);
                mListingAdapter.setData(String.format("%d %s", listingTotalCount, mSearchedEntities), mListings, mGroupListings, mRequestType);
            } else {
                mListingAdapter.setData(String.format("%d %s", listingTotalCount, mSearchedEntities), mListings, null, mRequestType);
            }

            if(listingTotalCount > mListings.size()) {
                mListingRecyclerView.setHasMoreItems(true);
            } else {
                mListingRecyclerView.setHasMoreItems(false);
            }
            mListingRecyclerView.setIsLoading(false);
        } else {
            mListings.clear();
            mListings.addAll(listings);
            mGroupListings.clear();
            if(groupListings != null) {
                mGroupListings.addAll(groupListings);
            }
        }
    }

    @Override
    public void onLoadMoreItems() {
        mSerpRequestCallback.loadMoreItems();

        //TODO temp for testing
/*        Analytics.with(getActivity()).track(JarvisConstants.DELIVERY_ID, new Properties()
                .putValue("listing_id", mListings.get(9).id)
                .putValue("serp_visible_item", 9)
                .putValue("serp_filter_budget", null)
                .putValue("serp_filter_property_type", null)

                .putValue("serp_filter_bhk", null));*/

        if(mIsScrollEventSent){
            return;
        }

        mIsScrollEventSent = true;
        Traits traits = new Traits();
        traits.put("serp_visible_item", mListings.size());
        Analytics.with(getActivity()).identify(traits);
        Analytics.with(getActivity()).flush();


    }

    @Override
    public void onScrolled(int dx, int dy, int state) {
        mSerpRequestCallback.onListScrolled(dx, dy, state);
    }
}
