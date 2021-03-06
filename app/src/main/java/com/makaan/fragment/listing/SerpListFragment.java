package com.makaan.fragment.listing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.SerpListingAdapter;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.ui.PaginatedListView;
import com.makaan.util.ErrorUtil;
import com.makaan.util.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/6/16.
 * Listing Fragment which will house a recycler view to show all the listing as per user's search criteria
 */
public class SerpListFragment extends MakaanBaseFragment implements PaginatedListView.PaginationListener{
    private static final String KEY_IS_CHILD_SERP = "is_child_serp";

    @Bind(R.id.fragment_listing_recycler_view)
    PaginatedListView mListingRecyclerView;
    /*@Bind(R.id.fragment_listing_total_properties_text_view)
    TextView mTotalPropertiesTextView;*/

    private SerpListingAdapter mListingAdapter;
    private LinearLayoutManager mLayoutManager;
    private String mSearchedEntities = "";

    private boolean mIsChildSerp;

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
                mListingAdapter.setData(String.format("%s %s", StringUtil.getFormattedNumber(mTotalCount),
                        mSearchedEntities), mListings, mGroupListings, mRequestType);
                if(mTotalCount > mListings.size()) {
                    mListingRecyclerView.setHasMoreItems(true);
                } else {
                    mListingRecyclerView.setHasMoreItems(false);
                }

                if(mTotalCount == 0) {
                    if((mRequestType & SerpActivity.MASK_LISTING_TYPE) == SerpActivity.TYPE_BUILDER
                            || (mRequestType & SerpActivity.MASK_LISTING_TYPE) == SerpActivity.TYPE_SELLER) {
                        showContent();
                    } else {
                        showNoResults("no results found", "set an alert");
                    }
                } else {
                    showContent();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void updateListings(ArrayList<Listing> listings, ArrayList<GroupListing> groupListings, SerpRequestCallback serpRequestCallback,
                               int requestType, int listingTotalCount, boolean isBuyContext) {
        this.mSerpRequestCallback = serpRequestCallback;
        this.mRequestType = requestType;
        if(groupListings == null && listings == null) {
            if(mListingAdapter != null) {
                mListingAdapter.setData(null, mRequestType);
            }
            return;
        }
        mTotalCount = listingTotalCount;

        /*StringBuilder builder = new StringBuilder();
        builder.append(" properties ");

        if(selectedSearches.size() > 0 && SearchSuggestionType.GOOGLE_PLACE.getValue()
                .equalsIgnoreCase(selectedSearches.get(0).type)) {
            builder.append("near ");
        } if(selectedSearches.size() > 0 && SearchSuggestionType.BUILDER.getValue()
                .equalsIgnoreCase(selectedSearches.get(0).type)
                || selectedSearches.size() > 0 && SearchSuggestionType.BUILDERCITY.getValue()
                .equalsIgnoreCase(selectedSearches.get(0).type)
                || ((mRequestType & SerpActivity.MASK_LISTING_TYPE) == SerpActivity.TYPE_SELLER)) {
            builder.append("by ");
        } else if(selectedSearches.size() > 0) {
            builder.append("in ");
        }

        if(selectedSearches != null && selectedSearches.size() > 0) {
            if(SearchSuggestionType.CITY.getValue().equals(selectedSearches.get(0).type)) {
                if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                    builder.append(selectedSearches.get(0).city.toLowerCase());
                }
            } else if(SearchSuggestionType.BUILDERCITY.getValue().equals(selectedSearches.get(0).type)) {
                if(!TextUtils.isEmpty(selectedSearches.get(0).builderName)) {
                    builder.append(selectedSearches.get(0).builderName.toLowerCase());
                }
                if(!TextUtils.isEmpty(selectedSearches.get(0).entityName)) {
                    builder.append(" in ");
                    builder.append(selectedSearches.get(0).entityName.toLowerCase());
                }
            } else {
                for(SearchResponseItem search : selectedSearches) {
                    if(!TextUtils.isEmpty(search.entityName)) {
                        if(SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(search.type)) {
                            builder.append(search.entityName.toLowerCase());
                        } else {
                            builder.append(String.format("%s, ", search.entityName.toLowerCase()));
                        }
                    }
                }
                if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                    builder.append(selectedSearches.get(0).city.toLowerCase());
                }
            }
        }
        mSearchedEntities = builder.toString();*/
        if(isBuyContext) {
            mSearchedEntities = " properties for sale";
        } else {
            mSearchedEntities = " properties for rent";
        }

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
                mListingAdapter.setData(String.format("%s %s", StringUtil.getFormattedNumber(listingTotalCount),
                        mSearchedEntities), mListings, mGroupListings, mRequestType);
            } else {
                mListingAdapter.setData(String.format("%s %s", StringUtil.getFormattedNumber(listingTotalCount),
                        mSearchedEntities), mListings, null, mRequestType);
            }

            if(listingTotalCount > mListings.size()) {
                mListingRecyclerView.setHasMoreItems(true);
            } else {
                mListingRecyclerView.setHasMoreItems(false);
            }
            mListingRecyclerView.setIsLoading(false);
            if(listingTotalCount == 0) {
                if((mRequestType & SerpActivity.MASK_LISTING_TYPE) == SerpActivity.TYPE_BUILDER
                    || (mRequestType & SerpActivity.MASK_LISTING_TYPE) == SerpActivity.TYPE_SELLER) {
                    showContent();
                } else {
                    showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, true), "set an alert");
                }
            } else {
                showContent();
            }
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
    }

    @Override
    public void onResume() {
        mListingAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected boolean onActionButtonClick() {
        if(mSerpRequestCallback != null) {
            mSerpRequestCallback.requestDetailPage(SerpActivity.REQUEST_SET_ALERT, null);
        }
        return true;
    }
}
