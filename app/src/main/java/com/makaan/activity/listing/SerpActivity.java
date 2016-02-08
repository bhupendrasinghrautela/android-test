package com.makaan.activity.listing;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.event.locality.GpByIdEvent;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.listing.ChildSerpClusterFragment;
import com.makaan.fragment.listing.FiltersDialogFragment;
import com.makaan.fragment.listing.SerpListFragment;
import com.makaan.fragment.listing.SerpMapFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.pojo.GroupCluster;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.serp.FilterGroup;
import com.makaan.service.BuilderService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SellerService;
import com.makaan.ui.listing.RelevancePopupWindowController;
import com.makaan.util.KeyUtil;
import com.makaan.util.Preference;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpActivity extends MakaanBaseSearchActivity implements SerpRequestCallback,
        FiltersDialogFragment.FilterDialogFragmentCallback {
    // type of request to open serp, it must be one of the following values
    public static final String REQUEST_TYPE = "type";

    // data to be used to create serp selector, String value
    // should be in form {filter1}:{filter_value_1},{filter_value_2};{filter2}:{filter_value_2_1},{filter_value_2_2}
    // except for TYPE_SUGGESTION, where data itself should be selector string
    public static final String REQUEST_DATA = "data";

    public static final String REQUEST_CONTEXT = "context";

    public static final int TYPE_UNKNOWN = 0x00;
    public static final int TYPE_GPID = 0x01;

    // for child serp
    public static final int TYPE_CLUSTER = 0x02;
    public static final int TYPE_SEARCH = 0x03;
    public static final int TYPE_SELLER = 0x04;
    public static final int TYPE_BUILDER = 0x05;
    public static final int TYPE_LOCALITY = 0x06;
    public static final int TYPE_SUBURB = 0x07;
    public static final int TYPE_CITY = 0x08;
    public static final int TYPE_PROJECT = 0x09;
    public static final int TYPE_SUGGESTION = 0x0a;
    public static final int TYPE_BUILDER_CITY = 0x0b;
    public static final int TYPE_TAXONOMY = 0x0c;
    public static final int TYPE_HOME = 0x0d;

    public static final int MASK_LISTING_TYPE = 0x0f;
    public static final int MASK_LISTING_UPDATE_TYPE = 0xf0;

    public static final int TYPE_SORT = 0x10;
    public static final int TYPE_LOAD_MORE = 0x20;
    public static final int TYPE_FILTER = 0x30;

    // view requests to get new apis
    public static final int REQUEST_BUILDER_API = 0x01;
    public static final int REQUEST_SELLER_API = 0x02;

    public static final int REQUEST_PROPERTY_PAGE = 1;
    public static final int REQUEST_PROJECT_PAGE = 2;
    public static final int REQUEST_LEAD_FORM = 3;


    private static final int MAX_ITEMS_TO_REQUEST = 20;
    private static final int MAX_GROUP_ITEMS_TO_REQUEST = 2 * GroupCluster.MAX_CLUSTERS_IN_GROUP;

    public static boolean isSellerSerp = false;

    private int mSelectedSortIndex = 0;

    private SerpListFragment mListingFragment;
    private SerpListFragment mChildSerpListFragment;
    private SerpListFragment mSellerSerpListFragment;

    @Bind(R.id.activity_serp_filters_frame_layout)
    FrameLayout mFiltersFrameLayout;
    @Bind(R.id.activity_serp_similar_properties_frame_layout)
    FrameLayout mSimilarPropertiesFrameLayout;
    @Bind(R.id.activity_serp_content_frame_layout)
    FrameLayout mContentFrameLayout;

    @Bind(R.id.fragment_filters_filter_relative_layout)
    RelativeLayout mFilterRelativeLayout;
    @Bind(R.id.fragment_filters_relevance_relative_layout)
    RelativeLayout mRelevanceRelativeLayout;

    @Bind(R.id.fragment_filters_applied_filter_count_text_view)
    TextView mAppliedFiltersCountTextView;
    @Bind(R.id.fragment_filters_relevance_text_view)
    TextView mSortTextView;

    @Bind(R.id.fragment_filters_map_image_view)
    ImageView mMapImageView;

    private SerpGetEvent mListingGetEvent;
    private boolean mIsMapFragment;
    private SerpMapFragment mMapFragment;
    private ProgressDialog mProgressDialog;
    private int mSerpRequestType = SerpActivity.TYPE_UNKNOWN;

    private boolean mGroupsRequested;
    private boolean mGroupReceived;
    private boolean mSerpReceived;
    private GroupSerpGetEvent mGroupListingGetEvent;
    private Long mChildListingId;
    private Selector mGroupSelector;

    private ArrayList<Listing> mListings;
    private ArrayList<GroupListing> mGroupListings;
    private int mListingPage;
    private int mListingCount;
    private int mGroupListingCount;
    private Long mChildSerpId;
    private ArrayList<Listing> mChildListings;
    private int mChildListingCount;
    private int mChildListingPage;

    public Selector mSerpSelector = new Selector();
    private ArrayList<FilterGroup> mFilterGroups;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_serp;
    }

    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for (FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    @Override
    protected SerpObjects getSerpObjects() {
        SerpObjects obj = new SerpObjects();
        obj.selector = mSerpSelector;
        obj.filterGroups = mFilterGroups;
        obj.isBuy = mSerpContext == SERP_CONTEXT_BUY;
        return obj;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if(intent != null) {
            mSerpContext = intent.getIntExtra(REQUEST_CONTEXT, SERP_CONTEXT_BUY) == SERP_CONTEXT_RENT ? SERP_CONTEXT_RENT : SERP_CONTEXT_BUY;
        } else {
            mSerpContext = Preference.getInt(getSharedPreferences(PreferenceConstants.PREF, MODE_PRIVATE), PreferenceConstants.PREF_CONTEXT, SERP_CONTEXT_BUY);
        }

        try {
            if (mSerpContext == SERP_CONTEXT_BUY) {
                mSerpSelector.term(KeyUtil.LISTING_CATEGORY, new String[] {"Primary", "Resale"});
                mFilterGroups = getClonedFilterGroups(MasterDataCache.getInstance().getAllBuyFilterGroups());
            } else {
                mSerpSelector.term(KeyUtil.LISTING_CATEGORY, new String[] {"Rental"});
                mFilterGroups = getClonedFilterGroups(MasterDataCache.getInstance().getAllRentFilterGroups());
            }
        } catch (CloneNotSupportedException ex) { }

        super.onCreate(savedInstanceState);

        // init fragments we need to use
        initUi(true);

        int type = SerpActivity.TYPE_UNKNOWN;
        generateGroupSelector();

        handleIntent(intent);

        setTitle(this.getResources().getString(R.string.search_default_hint));
    }

    private void handleIntent(Intent intent) {
        int type;
        if(intent != null) {
            type = intent.getIntExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_UNKNOWN);
            if(type == SerpActivity.TYPE_HOME){
                mSerpSelector.reset();
                parseSerpRequest(intent, SerpActivity.TYPE_HOME);

                openSearch(true);

            } else if (type == SerpActivity.TYPE_GPID) {
                parseSerpRequest(intent, SerpActivity.TYPE_GPID);
            } else if (type == SerpActivity.TYPE_PROJECT) {
                parseSerpRequest(intent, SerpActivity.TYPE_PROJECT);

            } else if (type == SerpActivity.TYPE_SEARCH) {
                mSerpSelector.removeTerm("builderId");
                mSerpSelector.removeTerm("projectId");
                mSerpSelector.removeTerm("localityId");
                mSerpSelector.removeTerm("cityId");
                mSerpSelector.removeTerm("suburbId");
                mSerpSelector.removeTerm("localityOrSuburbId");

                parseSerpRequest(intent, SerpActivity.TYPE_PROJECT);

            } else if (type == SerpActivity.TYPE_UNKNOWN) {
                // TODO check whether it should be used or not
                fetchData();
            } else if (type == SerpActivity.TYPE_SELLER) {
                mSerpSelector.removeTerm("builderId");
//                mSerpSelector.removeTerm("projectId");
                mSerpSelector.removeTerm("localityId");
                mSerpSelector.removeTerm("cityId");
                mSerpSelector.removeTerm("suburbId");
                mSerpSelector.removeTerm("listingCompanyId");
                parseSerpRequest(intent, type);
            } else {
                parseSerpRequest(intent, type);
            }
        } else {
            parseSerpRequest(intent, SerpActivity.TYPE_UNKNOWN);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void parseSerpRequest(Intent intent, int type) {
        if(intent.hasExtra(REQUEST_DATA)) {
            SerpRequest request = intent.getParcelableExtra(REQUEST_DATA);
            request.applySelector(mSerpSelector, mFilterGroups);

            if(type == TYPE_GPID) {
                serpRequest(TYPE_GPID, mSerpSelector, request.getGpId());
            }
        }
        if(type != TYPE_HOME && type != TYPE_GPID) {
            serpRequest(type, mSerpSelector);
        }
    }

    private void generateGroupSelector() {
        mGroupSelector = mSerpSelector.clone();
        mGroupSelector.field("groupingAttributes").field("groupedListings").field("listing").field("id");
        mGroupSelector.page(0, (2 * GroupCluster.MAX_CLUSTERS_IN_GROUP));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SerpObjects.getAppliedFilterCount(mFilterGroups) != 0) {
            mAppliedFiltersCountTextView.setVisibility(View.VISIBLE);
            mAppliedFiltersCountTextView.setText(String.valueOf(SerpObjects.getAppliedFilterCount(mFilterGroups)));
        } else {
            mAppliedFiltersCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            mSerpRequestType = TYPE_SEARCH;
            setShowSearchBar(true, false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            mMapImageView.setImageResource(R.drawable.map_icon);

            mSerpSelector.removePaging();
        } else if(mIsMapFragment) {
            mIsMapFragment = false;
            mMapImageView.setImageResource(R.drawable.map_icon);
            mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, (mSerpRequestType & MASK_LISTING_TYPE), mListingCount);
        }
    }

    protected void initUi(boolean showSearchBar) {
        super.initUi(true);
    }

    private void fetchData() {
        mSerpSelector.term("cityId", "11").term("listingCategory", new String[]{"Primary"});
        serpRequest(SerpActivity.TYPE_UNKNOWN, mSerpSelector);
    }

    @Subscribe
    public synchronized void onResults(SerpGetEvent listingGetEvent) {
        if(mIsMapFragment && mMapFragment != null) {
            mListingGetEvent = listingGetEvent;
            updateListings(listingGetEvent, null);
            mMapFragment.setData(mListings);
        } else if (isSellerSerp) {
            setShowSearchBar(false, false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            if (mSellerSerpListFragment == null || !mSellerSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                childSerpClusterFragment.setData(mGroupListings, mChildSerpId, this);
                // create new listing fragment to show the listings
                mSellerSerpListFragment = SerpListFragment.init(true);
                updateListings(listingGetEvent, null);
                mSellerSerpListFragment.updateListings(mListings, null, getSelectedSearches(), this, mSerpRequestType, mListingCount);

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                updateListings(listingGetEvent, null);
                mSellerSerpListFragment.updateListings(mListings, null, getSelectedSearches(), this, mSerpRequestType, mListingCount);
            }
        } else if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            setShowSearchBar(false, false);
            mFiltersFrameLayout.setVisibility(View.GONE);
            mSimilarPropertiesFrameLayout.setVisibility(View.VISIBLE);
            if (mChildSerpListFragment == null || !mChildSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                childSerpClusterFragment.setData(mGroupListings, mChildSerpId, this);
                // create new listing fragment to show the listings
                mChildSerpListFragment = SerpListFragment.init(true);
                updateListings(listingGetEvent, null);
                mChildSerpListFragment.updateListings(mChildListings, null, getSelectedSearches(), this, mSerpRequestType, mChildListingCount);

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                updateListings(listingGetEvent, null);
                mChildSerpListFragment.updateListings(mChildListings, null, getSelectedSearches(), this, mSerpRequestType, mChildListingCount);
            }
        } else {
            mListingGetEvent = listingGetEvent;
            // for buider, seller and normal serp
            if(!mGroupsRequested || mGroupReceived) {
                setShowSearchBar(true, false);
                mFiltersFrameLayout.setVisibility(View.VISIBLE);
                mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
                // check if we have working listing fragment at this time
                if (mListingFragment == null) {
                    // create new listing fragment to show the listings
                    mListingFragment = SerpListFragment.init(false);

                    updateListings(listingGetEvent, mGroupListingGetEvent);
                    mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount);
                    initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                } else {
                    // update already running listing fragment with the new list
                    updateListings(listingGetEvent, mGroupListingGetEvent);
                    mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount);
                }
            }
        }
        mSerpReceived = true;

        if(mProgressDialog != null) {
            if(mGroupReceived) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Subscribe
    public synchronized void onResults(GroupSerpGetEvent groupListingGetEvent) {
        mGroupListingGetEvent = groupListingGetEvent;
        mGroupReceived = true;

        if(mSerpReceived) {
            setShowSearchBar(true, false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);

            // check if we have working listing fragment at this time
            if (mListingFragment == null) {
                // create new listing fragment to show the listings
                mListingFragment = SerpListFragment.init(false);
                updateListings(mListingGetEvent, mGroupListingGetEvent);
                mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount);
                initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            } else {
                // update already running listing fragment with the new list
                updateListings(mListingGetEvent, mGroupListingGetEvent);
                mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount);
            }


            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }



    public void updateListings(SerpGetEvent listingGetEvent, GroupSerpGetEvent groupListingGetEvent) {
        if(listingGetEvent == null || listingGetEvent.listingData == null) {
            return;
        }
        ArrayList<Listing> listings;
        boolean isChildSerp = (mSerpRequestType & MASK_LISTING_TYPE) == TYPE_CLUSTER;
        if(isChildSerp) {
            if(mChildListings == null) {
                mChildListings = new ArrayList<>();
            }
            listings = mChildListings;
        } else {
            if(mListings == null) {
                mListings = new ArrayList<>();
            }
            listings = mListings;
        }
        if(mGroupListings == null) {
            mGroupListings = new ArrayList<>();
        }

        if((mSerpRequestType & SerpActivity.MASK_LISTING_UPDATE_TYPE) != SerpActivity.TYPE_LOAD_MORE) {
            listings.clear();
            if(!isChildSerp) {
                mGroupListings.clear();
                mGroupListingCount = 0;
                mListingPage = 0;
                mListingCount = 0;
            } else {
                mChildListingPage = 0;
                mChildListingCount = 0;
            }
        }
        listings.addAll(listingGetEvent.listingData.listings);
        if(isChildSerp) {
            mChildListingCount = listingGetEvent.listingData.totalCount;
        } else {
            mListingCount = listingGetEvent.listingData.totalCount;
        }

        if(!isChildSerp && groupListingGetEvent != null && groupListingGetEvent.groupListingData != null) {
            mGroupListings.addAll(groupListingGetEvent.groupListingData.groupListings);
            mGroupListingCount = groupListingGetEvent.groupListingData.totalCount;
        }
    }

    @OnClick(R.id.fragment_filters_filter_relative_layout)
    public void onFilterPressed(View view) {
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        FiltersDialogFragment filterFragment = FiltersDialogFragment.init();
        filterFragment.show(ft, "Filters");
    }

    @OnClick(R.id.fragment_filters_relevance_relative_layout)
    public void onRelevancePressed(View view) {
        new RelevancePopupWindowController().showRelevancePopupWindow(this, mRelevanceRelativeLayout,
                new RelevancePopupWindowController.RelevancePopupWindowCallback() {
                    @Override
                    public void popupWindowDismissed() {
                        mMainFrameLayout.getForeground().setAlpha(0);
                    }

                    @Override
                    public void sortSelected(String sort, String fieldName, String value, int i) {
                        mSortTextView.setText(sort);
                        mSerpSelector.sort(fieldName, value);
                        mSelectedSortIndex = i;
                        SerpActivity.this.serpRequest(SerpActivity.TYPE_SORT, mSerpSelector);
                    }
                }, mSelectedSortIndex);
        mMainFrameLayout.getForeground().setAlpha(128);
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }

    @Override
    protected boolean needScrollableSearchBar() {
        return true;
    }

    @Override
    protected boolean supportsListing() {
        return true;
    }

    @Subscribe
    public void onResults(GpByIdEvent gpIdResultEvent) {
        if(gpIdResultEvent == null || gpIdResultEvent.gpDetail == null) {
            return;
        }
        mSerpSelector.term("cityId", String.valueOf(gpIdResultEvent.gpDetail.cityid));
        ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                .handleSerpRequest(mSerpSelector, gpIdResultEvent.gpDetail.placeId, true, mGroupSelector);
        showProgress();
    }

    @OnClick(R.id.fragment_filters_map_image_view)
    public void onMapViewPressed(View view) {
        if(mIsMapFragment) {
            if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                mMapImageView.setImageResource(R.drawable.map_icon);
                mIsMapFragment = false;
                mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, (mSerpRequestType & MASK_LISTING_TYPE), mListingCount);
            }
            /*mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, (mSerpRequestType & MASK_LISTING_TYPE), mListingCount);
            initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, true);
            mIsMapFragment = false;
            mMapImageView.setImageResource(R.drawable.map_icon);*/
        } else {
            if (mListingGetEvent != null) {
                mMapFragment = new SerpMapFragment();
                mMapFragment.setData(mListings);
                initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, true);
                mIsMapFragment = true;
                mMapImageView.setImageResource(R.drawable.list);
            }
        }
    }

    @Override
    public void dialogDismissed() {
        if(SerpObjects.getAppliedFilterCount(mFilterGroups) != 0) {
            mAppliedFiltersCountTextView.setVisibility(View.VISIBLE);
            mAppliedFiltersCountTextView.setText(String.valueOf(SerpObjects.getAppliedFilterCount(mFilterGroups)));
        } else {
            mAppliedFiltersCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public void serpRequest(int type, Selector selector) {
        serpRequest(type, selector, true);
    }

    public void serpRequest(int type, Selector selector, boolean needGrouping) {
        if((type & MASK_LISTING_TYPE) > 0) {
            mSerpRequestType = type;
        } else {
            mSerpRequestType = (mSerpRequestType & MASK_LISTING_TYPE) | type;
        }

        if(type != TYPE_LOAD_MORE) {
            showProgress();

            // remove paging if we are loading new serp results
            selector.removePaging();
            selector.page(0, 20);
        }

        // if new serp is getting loaded
        // then remove builder or seller ids
        if((type & MASK_LISTING_TYPE) > 0) {
            if(((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_SELLER)) {
                selector.removeTerm("builderId");
            } else if(((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_BUILDER)) {
                selector.removeTerm("listingCompanyId");
            } else {
                selector.removeTerm("builderId");
                selector.removeTerm("listingCompanyId");
                generateGroupSelector();
            }
        } else if((type & MASK_LISTING_UPDATE_TYPE) == TYPE_FILTER) {
            generateGroupSelector();
        }

        if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_CLUSTER) {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                    .handleChildSerpRequest(mSerpSelector, mChildListingId);
            mGroupsRequested = false;
            mGroupReceived = true;
            mSerpReceived = false;
        } else if(((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_BUILDER)
                || ((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_SELLER)) {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).handleSerpRequest(selector);
            mGroupsRequested = false;
            mGroupReceived = true;
            mSerpReceived = false;
        } else {

            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).handleSerpRequest(selector, needGrouping, mGroupSelector);
            mGroupsRequested = needGrouping;
            mSerpReceived = false;
            mGroupReceived = !needGrouping;
        }
    }

    @Override
    public void serpRequest(int type, Long id) {
        mChildListingId = id;
        if((type & MASK_LISTING_TYPE) > 0) {
            mSerpRequestType = type;
            mSerpSelector.removePaging();
            mSerpSelector.page(0, MAX_ITEMS_TO_REQUEST);
        } else {
            mSerpRequestType = (mSerpRequestType & MASK_LISTING_TYPE) | type;
        }
        if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_CLUSTER) {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                    .handleChildSerpRequest(mSerpSelector, id);
            mChildSerpId = id;
            mGroupsRequested = false;
            mGroupReceived = true;
            mSerpReceived = false;
        }
        showProgress();
    }

    @Override
    public void serpRequest(int type, Selector selector, String gpId) {
        // TODO save selector so that we can use it when we get google place detail callback from below call
        if (type == TYPE_GPID) {
            ((LocalityService)MakaanServiceFactory.getInstance().getService(LocalityService.class)).getGooglePlaceDetail(gpId);
            mGroupsRequested = true;
            mSerpReceived = false;
            mGroupReceived = false;
        }
        showProgress();
    }

    @Override
    public void serpRequest(int typeSuggestion, String filters) {
        parseAndApplyFilter(filters);
    }

    @Override
    public void requestApi(int request, String key) {
        if(request == REQUEST_BUILDER_API) {
            HashSet<String> ids = mSerpSelector.getTerm(key);
            if(ids.size() == 1) {
                ((BuilderService)MakaanServiceFactory.getInstance().getService(BuilderService.class))
                        .getBuilderById(Long.valueOf((String)(ids.toArray()[0])));
            }
        } else if(request == REQUEST_SELLER_API) {
            HashSet<String> ids = mSerpSelector.getTerm(key);
            if(ids.size() == 1) {
                ((SellerService)MakaanServiceFactory.getInstance().getService(SellerService.class))
                        .getSellerById(Long.valueOf((String)(ids.toArray()[0])));
            }
        }
    }

    @Override
    public boolean needSellerInfoInSerp() {
        if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_SELLER) {
            return false;
        }
        return true;
    }

    @Override
    public Selector getGroupSelector() {
        return mGroupSelector;
    }

    @Override
    public void requestDetailPage(int type, Bundle bundle) {
        if(type == REQUEST_PROPERTY_PAGE) {
            Intent intent = new Intent(this, PropertyActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(type == REQUEST_PROJECT_PAGE) {
            Intent intent = new Intent(this, ProjectActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(type == REQUEST_LEAD_FORM) {
            Intent intent = new Intent(this, LeadFormActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void loadMoreItems() {
        if(mListings != null) {
            if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_CLUSTER) {
                if ((mChildListings.size() / MAX_ITEMS_TO_REQUEST) == (mChildListingPage + 1) && mChildListings.size() < mChildListingCount) {
                    mChildListingPage++;
                    mSerpSelector.page(mChildListings.size(), MAX_ITEMS_TO_REQUEST);

                    serpRequest(SerpActivity.TYPE_LOAD_MORE, mChildListingId);
                }
            } else {
                if ((mListings.size() / MAX_ITEMS_TO_REQUEST) == (mListingPage + 1) && mListings.size() < mListingCount) {
                    mListingPage++;
                    mSerpSelector.page(mListings.size(), MAX_ITEMS_TO_REQUEST);

                    if (mGroupListingCount > mGroupListings.size()) {
                        Selector groupSelector = getGroupSelector();
                        groupSelector.page(mGroupListings.size(), MAX_GROUP_ITEMS_TO_REQUEST);
                        serpRequest(SerpActivity.TYPE_LOAD_MORE, mSerpSelector, true);
                    } else {
                        serpRequest(SerpActivity.TYPE_LOAD_MORE, mSerpSelector, false);
                    }

                }
            }
        }
    }

    private void showProgress() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait while loading...");
        //mProgressDialog.setCancelable(false);
        //mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        displayPopupWindow(event.message);
    }

    private void parseAndApplyFilter(String json) {
        mSerpSelector.reset();
        GsonBuilder builder = new GsonBuilder();
        Object o = builder.create().fromJson(json, Object.class);
        parseObject(o);
    }

    private void parseObject(Object obj) {
        if(obj instanceof LinkedTreeMap<?,?>) {
            for(Object key : ((LinkedTreeMap) obj).keySet()) {
                if(key instanceof String) {
                    Object obj2 = ((LinkedTreeMap) obj).get(key);
                    if(obj2 instanceof ArrayList<?>
                            || obj2 instanceof LinkedTreeMap<?,?>) {
                        parseObject(obj2);
                    } else if(obj2 instanceof String
                            || obj2 instanceof Double
                            || obj2 instanceof Integer) {
                        if("localityId".equals(key)) {
                            mSerpSelector.term((String) key, String.valueOf(obj2));
                        }
                    }
                }
            }
        } else if(obj instanceof ArrayList<?>) {
            for(Object obj2 : ((ArrayList) obj)) {
                if(obj2 instanceof LinkedTreeMap<?,?>) {
                    parseObject(obj2);
                }
            }
        }
    }

    @Override
    protected boolean areListingsAvailable() {
        if(mListings == null) {
            return false;
        }
        return true;
    }
}
