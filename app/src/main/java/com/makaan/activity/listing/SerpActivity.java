package com.makaan.activity.listing;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.event.locality.GpByIdEvent;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.listing.ChildSerpClusterFragment;
import com.makaan.fragment.listing.FiltersDialogFragment;
import com.makaan.fragment.listing.SerpListFragment;
import com.makaan.fragment.listing.SerpMapFragment;
import com.makaan.fragment.listing.SetAlertsDialogFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.event.PageTag;
import com.makaan.pojo.GroupCluster;
import com.makaan.pojo.SerpBackStack;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.request.selector.Selector;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.serp.FilterGroup;
import com.makaan.service.BuilderService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SellerService;
import com.makaan.ui.listing.RelevancePopupWindowController;
import com.makaan.ui.view.MPlusBadgePopupDialog;
import com.makaan.util.ErrorUtil;
import com.makaan.util.KeyUtil;
import com.makaan.util.Preference;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpActivity extends MakaanBaseSearchActivity implements SerpRequestCallback,
        FiltersDialogFragment.FilterDialogFragmentCallback,FiltersDialogFragment.FilterStringListener {
    public static final String SCREEN_NAME = "serp";

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
    public static final int TYPE_NEARBY = 0x0e;
    public static final int TYPE_NOTIFICATION = 0x0f;

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
    public static final int REQUEST_SET_ALERT = 4;
    public static final int REQUEST_MPLUS_POPUP = 5;


    private static final int MAX_ITEMS_TO_REQUEST = 20;
    private static final int MAX_GROUP_ITEMS_TO_REQUEST = 2 * GroupCluster.MAX_CLUSTERS_IN_GROUP;

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
    private boolean mFilterDialogOpened;
    private Properties mProperties;

    SerpBackStack mSerpBackStack = new SerpBackStack();
    private Long mLastUsedClusterId;

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
        if(intent != null && intent.hasExtra(REQUEST_CONTEXT)) {
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
    }

    private void handleIntent(Intent intent) {
        int type;
        if(intent != null) {
            type = intent.getIntExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_UNKNOWN);
            if(type == SerpActivity.TYPE_HOME) {
                mSerpSelector.reset();
                parseSerpRequest(intent, SerpActivity.TYPE_HOME);

                openSearch(true);

            } else if (type == SerpActivity.TYPE_CITY) {
                removeAllSelectors();
                parseSerpRequest(intent, SerpActivity.TYPE_CITY);
            } else if (type == SerpActivity.TYPE_GPID) {
                removeAllSelectors();
                parseSerpRequest(intent, SerpActivity.TYPE_GPID);
            } else if (type == SerpActivity.TYPE_PROJECT) {
                removeAllSelectors();
                parseSerpRequest(intent, SerpActivity.TYPE_PROJECT);

            } else if (type == SerpActivity.TYPE_SEARCH) {
                removeAllSelectors();

                parseSerpRequest(intent, SerpActivity.TYPE_SEARCH);

            } else if (type == SerpActivity.TYPE_BUILDER
                    || type == SerpActivity.TYPE_BUILDER_CITY) {
                removeAllSelectors();

                parseSerpRequest(intent, SerpActivity.TYPE_BUILDER);
            } else if (type == SerpActivity.TYPE_SELLER) {
//                mSerpSelector.removeTerm("builderId");
//                mSerpSelector.removeTerm("projectId");
//                mSerpSelector.removeTerm("localityId");
//                mSerpSelector.removeTerm("cityId");
//                mSerpSelector.removeTerm("suburbId");
                mSerpSelector.removeTerm("listingCompanyId");
                parseSerpRequest(intent, type);
            } else if (type == SerpActivity.TYPE_NEARBY) {
                removeAllSelectors();
                parseSerpRequest(intent, type);
            } else if (type == SerpActivity.TYPE_SUGGESTION) {
                removeAllSelectors();
                parseSerpRequest(intent, type);
            } else if(type == SerpActivity.TYPE_NOTIFICATION) {
                removeAllSelectors();
                parseSerpRequest(intent, type);
            } else {
                parseSerpRequest(intent, type);
            }
        } else {
            parseSerpRequest(intent, SerpActivity.TYPE_UNKNOWN);
        }
    }

    private void removeAllSelectors() {
        mSerpSelector.removeGeo();
        mSerpSelector.removeFacets();
        mSerpSelector.removeTerm("builderId");
        mSerpSelector.removeTerm("projectId");
        mSerpSelector.removeTerm("localityId");
        mSerpSelector.removeTerm("cityId");
        mSerpSelector.removeTerm("suburbId");
        mSerpSelector.removeTerm("localityOrSuburbId");
        mSerpSelector.removeTerm("listingCompanyId");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void parseSerpRequest(Intent intent, int type) {

        if(intent.hasExtra(REQUEST_DATA)) {

            SerpRequest request = intent.getParcelableExtra(REQUEST_DATA);

            if((type & MASK_LISTING_TYPE) > 0) {
                if(type == TYPE_SELLER) {
                    SearchResponseItem item = new SearchResponseItem();
                    item.type = SearchSuggestionType.SELLER.getValue();
                    item.displayText = request.getTitle();
                    item.entityName = request.getTitle();
                    request.setSearch(item);
                }

                if (!request.isFromBackstack()) {
                    if (type == SerpActivity.TYPE_GPID || type == SerpActivity.TYPE_NEARBY) {
                        setSearchBarCollapsible(false);
                        mIsMapFragment = true;
                        mMapImageView.setImageResource(R.drawable.list);
                        setJarvisVisibility(false);
                    } else {
                        setSearchBarCollapsible(true);
                        mIsMapFragment = false;
                        mMapImageView.setImageResource(R.drawable.map_icon);
                        setJarvisVisibility(true);
                    }
                } else {
                    if (mSerpBackStack.peekType() == SerpBackStack.TYPE_DEFAULT) {
                        setSearchBarCollapsible(true);
                        mIsMapFragment = false;
                        mMapImageView.setImageResource(R.drawable.map_icon);
                        setJarvisVisibility(true);
                    } else {
                        setSearchBarCollapsible(false);
                        mIsMapFragment = true;
                        mMapImageView.setImageResource(R.drawable.list);
                        setJarvisVisibility(false);
                    }
                }

                SerpRequest clonedRequest = mSerpBackStack.addToBackstack(request, mIsMapFragment ? SerpBackStack.TYPE_MAP : SerpBackStack.TYPE_DEFAULT);
                addSerpScrollTrackStatus(clonedRequest);
                if(request.getSearches() != null && request.getSearches().size() > 0) {
                    applySearch(request.getSearches());
                }
            }
            request.applySelector(mSerpSelector, mFilterGroups);
            if(type == TYPE_SUGGESTION || type == TYPE_NOTIFICATION) {
                if(request.getBuilderId() >= 0) {
                    type = TYPE_BUILDER;
                } else if(request.getSellerId() >= 0) {
                    type = TYPE_SELLER;
                }
            }

            String title = request.getTitle();
            if(TextUtils.isEmpty(title)) {
                setTitle(this.getResources().getString(R.string.search_default_hint));
            } else {
                if(request.getSearches() != null && request.getSearches().size() > 0
                        && SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(request.getSearches().get(0).type)) {

                    setTitle("near " + title);
                } else {
                    setTitle(title);
                }
            }

            if(type == TYPE_GPID) {
                serpRequest(TYPE_GPID, mSerpSelector, request.getGpId());
            }/* else if(type == TYPE_SEARCH) {
                applySearch(request.getSearches());
            }*/
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
        if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            super.onBackPressed();
            mSerpRequestType = mSerpBackStack.peek().getType();
            setShowSearchBar(true, false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            mMapImageView.setImageResource(R.drawable.map_icon);
            setSearchBarCollapsible(true);

            mSerpSelector.removePaging();
        } else {
            if(needBackProcessing()) {
                super.onBackPressed();
            } else if(mSerpBackStack.popFromBackstack(this)) {
                if(mIsMapFragment && mSerpBackStack.peekType() != SerpBackStack.TYPE_MAP) {
                    if (mListingFragment == null) {
                        mListingFragment = SerpListFragment.init(false);
                        initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                    } else {
                        initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                    }
                } else if(!mIsMapFragment && mSerpBackStack.peekType() != SerpBackStack.TYPE_DEFAULT) {
                    if (mMapFragment == null) {
                        mMapFragment = new SerpMapFragment();
                        initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, false);
                    } else {
                        initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, false);
                    }
                }
                return;
            } else {
                super.onBackPressed();
            }
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
        if(isActivityDead()){
            return;
        }

        if(null==listingGetEvent|| null!=listingGetEvent.error){
            mSerpReceived = true;
            if(listingGetEvent.error != null && listingGetEvent.error.error != null
                    && listingGetEvent.error.error.networkResponse != null) {
                showNoResults(ErrorUtil.getErrorMessageId(listingGetEvent.error.error.networkResponse.statusCode, true));
            } else {
                if(listingGetEvent.error != null && !TextUtils.isEmpty(listingGetEvent.error.msg)) {
                    showNoResults(listingGetEvent.error.msg);
                } else {
                    showNoResults();
                }
            }
            return;
        }
        if(listingGetEvent.listingData == null || listingGetEvent.listingData.totalCount == 0
                || listingGetEvent.listingData.listings == null || listingGetEvent.listingData.listings.size() == 0) {
            if((mSerpRequestType & MASK_LISTING_UPDATE_TYPE) == TYPE_FILTER) {
                if(mListingGetEvent != null && mListingGetEvent.listingData != null
                        && mListingGetEvent.listingData.listings != null
                        && mListingGetEvent.listingData.listings.size() > 0) {
                    if(mProperties !=null){
                        StringBuilder stringBuilder=new StringBuilder();
                        String space=" ";
                        Properties properties1=MakaanEventPayload.beginBatch();
                        properties1.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorUsability);
                        if(mSerpBackStack != null && mSerpBackStack.peek() != null
                                && mSerpBackStack.peek().getSearches() != null
                                && mSerpBackStack.peek().getSearches().size() >= 0) {
                            for(SearchResponseItem item : mSerpBackStack.peek().getSearches()) {
                                if(item != null && item.displayText!=null && !TextUtils.isEmpty(item.displayText)) {
                                    stringBuilder.append(item.displayText);
                                    stringBuilder.append(space);
                                }
                            }
                        }
                        properties1.put(MakaanEventPayload.LABEL, String.format("%s_%s", stringBuilder.toString(), mProperties.toString()));
                        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.noPropertyMatchingSearch);
                    }
                }
            }
        }

        if(mIsMapFragment) {
            if(mMapFragment != null) {
                mListingGetEvent = listingGetEvent;
                updateListings(listingGetEvent, null);
                mMapFragment.setData(mListings, mListingCount, this, mSerpRequestType);

                if((mSerpRequestType & MASK_LISTING_UPDATE_TYPE) == 0) {
                    initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, false);
                }
            } else {
                mMapFragment = new SerpMapFragment();
                mListingGetEvent = listingGetEvent;
                updateListings(listingGetEvent, null);
                mMapFragment.setData(mListings, mListingCount, this, mSerpRequestType);
                initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, false);
            }
        } else if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            setShowSearchBar(false, false);
            mFiltersFrameLayout.setVisibility(View.GONE);
            mSimilarPropertiesFrameLayout.setVisibility(View.VISIBLE);
            if (mChildSerpListFragment == null || !mChildSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                childSerpClusterFragment.setData(mGroupListings, mChildSerpId, this, mChildListingId);
                // create new listing fragment to show the listings
                mChildSerpListFragment = SerpListFragment.init(true);
                updateListings(listingGetEvent, null);
                mChildSerpListFragment.updateListings(mChildListings, null, getSelectedSearches(), this, mSerpRequestType, mChildListingCount, mSerpContext == SERP_CONTEXT_BUY);

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                updateListings(listingGetEvent, null);
                mChildSerpListFragment.updateListings(mChildListings, null, getSelectedSearches(), this, mSerpRequestType, mChildListingCount, mSerpContext == SERP_CONTEXT_BUY);
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
                    mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount, mSerpContext == SERP_CONTEXT_BUY);
                    initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                } else {
                    // update already running listing fragment with the new list
                    updateListings(listingGetEvent, mGroupListingGetEvent);
                    mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount, mSerpContext == SERP_CONTEXT_BUY);

                    if((mSerpRequestType & MASK_LISTING_UPDATE_TYPE) == 0) {
                        initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                    }
                }
            }
        }
        mSerpReceived = true;

//        if(mProgressDialog != null) {
        if(mGroupReceived) {
            showContent();
        }
//        }
    }

    @Subscribe
    public synchronized void onResults(GroupSerpGetEvent groupListingGetEvent) {
        if(isActivityDead()){
            return;
        }


        if(null==groupListingGetEvent|| null!=groupListingGetEvent.error){
            mGroupReceived = true;
            return;
        }
        mGroupListingGetEvent = groupListingGetEvent;
        mGroupReceived = true;
        if(mIsMapFragment) {
            updateListings(mListingGetEvent, mGroupListingGetEvent);
            if(mSerpReceived) {
                showContent();
            }
            return;
        }

        if(mSerpReceived) {
            setShowSearchBar(true, false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);

            // check if we have working listing fragment at this time
            if (mListingFragment == null) {
                // create new listing fragment to show the listings
                mListingFragment = SerpListFragment.init(false);
                updateListings(mListingGetEvent, mGroupListingGetEvent);
                mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount, mSerpContext == SERP_CONTEXT_BUY);
                initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            } else {
                // update already running listing fragment with the new list
                updateListings(mListingGetEvent, mGroupListingGetEvent);
                mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, mSerpRequestType, mListingCount, mSerpContext == SERP_CONTEXT_BUY);
            }


            showContent();
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

        setTrackUrl();
    }

    @OnClick(R.id.fragment_filters_filter_relative_layout)
    public void onFilterPressed(View view) {
        if(mFilterDialogOpened == true) {
            return;
        }
        mFilterDialogOpened = true;
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        FiltersDialogFragment filterFragment = FiltersDialogFragment.init();
        filterFragment.show(ft, "Filters");
    }

    @OnClick(R.id.fragment_filters_relevance_relative_layout)
    public void onRelevancePressed(View view) {
        final Context context =this;
        new RelevancePopupWindowController().showRelevancePopupWindow(this, mRelevanceRelativeLayout,
                new RelevancePopupWindowController.RelevancePopupWindowCallback() {
                    @Override
                    public void popupWindowDismissed() {
                        mMainFrameLayout.getForeground().setAlpha(0);
                    }

                    @Override
                    public void sortSelected(String sort, String fieldName, String value, int i) {
                        Properties properties = MakaanEventPayload.beginBatch();
                        if(!TextUtils.isEmpty(fieldName)) {
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                            if (fieldName.equalsIgnoreCase(MakaanEventPayload.PRICE)) {
                                properties.put(MakaanEventPayload.LABEL, sort);
                            } else if (sort.equalsIgnoreCase(MakaanEventPayload.SELLER_RATING)) {
                                properties.put(MakaanEventPayload.LABEL, sort);
                            } else if (sort.equalsIgnoreCase(MakaanEventPayload.DATE_POSTED)) {
                                properties.put(MakaanEventPayload.LABEL, sort);
                            }
                            MakaanEventPayload.endBatch(context, MakaanTrackerConstants.Action.sortProperty);
                        }

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
        if(isActivityDead()){
            return;
        }
        if(null==gpIdResultEvent|| null!=gpIdResultEvent.error){
            return;
        }

        if(gpIdResultEvent.gpDetail == null) {
            return;
        }

        if(mSerpBackStack.peek() != null && mSerpBackStack.peek().getType() == TYPE_GPID) {
            mSerpBackStack.peek().setCityId(gpIdResultEvent.gpDetail.cityid);
            mSerpRequestType = TYPE_GPID;
        } else {
            mSerpRequestType = TYPE_GPID;
        }

        mSerpSelector.page(0, MAX_ITEMS_TO_REQUEST);
        if(mGroupSelector != null) {
            mGroupSelector.reset();
        }
        generateGroupSelector();

        mSerpSelector.term("cityId", String.valueOf(gpIdResultEvent.gpDetail.cityid));
        ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                .handleSerpRequest(mSerpSelector, gpIdResultEvent.gpDetail.placeId, true, mGroupSelector);
        showProgress();
    }

    @OnClick(R.id.fragment_filters_map_image_view)
    public void onMapViewPressed(View view) {
        if(mIsMapFragment) {
            Properties properties =MakaanEventPayload.beginBatch();
            properties.putValue(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.listView);
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.searchMap);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerpViewSelection);

            SerpRequest clonedRequest = mSerpBackStack.addToBackstack(mSerpBackStack.peek(), SerpBackStack.TYPE_DEFAULT);
            addSerpScrollTrackStatus(clonedRequest);
            mMapImageView.setImageResource(R.drawable.map_icon);
            setSearchBarCollapsible(true);
            mIsMapFragment = false;
            if(mListingFragment == null) {
                mListingFragment = SerpListFragment.init(false);
            }
            mListingFragment.updateListings(mListings, mGroupListings, getSelectedSearches(), this, (mSerpRequestType & MASK_LISTING_TYPE), mListingCount, mSerpContext == SERP_CONTEXT_BUY);
            initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            setJarvisVisibility(true);
        } else {
            Properties properties =MakaanEventPayload.beginBatch();
            properties.putValue(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.map);
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.searchMap);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerpViewSelection);

            if (mListingGetEvent != null) {
                SerpRequest clonedRequest = mSerpBackStack.addToBackstack(mSerpBackStack.peek(), SerpBackStack.TYPE_MAP);
                addSerpScrollTrackStatus(clonedRequest);
                if(mMapFragment == null) {
                    mMapFragment = new SerpMapFragment();
                }
                mMapFragment.setData(mListings, mListingCount, this, (mSerpRequestType & MASK_LISTING_TYPE));
                initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, false);
                mIsMapFragment = true;
                mMapImageView.setImageResource(R.drawable.list);
                setSearchBarCollapsible(false);
                setJarvisVisibility(false);
            }
        }
    }

    @Override
    public void dialogDismissed() {
        mFilterDialogOpened = false;
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
    public String getScreenName() {
        return SCREEN_NAME;
    }

    protected String childScreenName() {
        if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_SELLER) {
            return "seller";
        } else if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_BUILDER) {
            return "builder";
        } else {
            return null;
        }
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
            mLastUsedClusterId = id;
        }
        dismissPopupWithAnim();
        showProgress();
    }

    @Override
    public Long getLastUsedClusterId() {
        Long id = mLastUsedClusterId;
        mLastUsedClusterId = null;
        return id;
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
        return (mSerpRequestType & MASK_LISTING_TYPE) != TYPE_SELLER;
    }

    @Override
    public Selector getGroupSelector() {
        return mGroupSelector;
    }

    @Override
    public void requestDetailPage(int type, Bundle bundle) {
        if(type == REQUEST_PROPERTY_PAGE) {
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(type == REQUEST_PROJECT_PAGE) {
            Intent intent = new Intent(this, OverviewActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if(type == REQUEST_LEAD_FORM) {
            Intent intent = new Intent(this, LeadFormActivity.class);
            intent.putExtra(KeyUtil.SOURCE_LEAD_FORM,SerpActivity.class.getName());
            intent.putExtras(bundle);
            startActivityForResult(intent, LeadFormActivity.LEAD_DROP_REQUEST);
        } else if(type == REQUEST_SET_ALERT) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            SetAlertsDialogFragment dialog = new SetAlertsDialogFragment();
            if(mSerpBackStack.peek() != null) {
                dialog.setData(mFilterGroups, mSerpBackStack.peek(), mSerpContext == SERP_CONTEXT_BUY, this);
            } else {
                dialog.setData(mFilterGroups, mListingGetEvent, mSerpContext == SERP_CONTEXT_BUY, this);
            }
            dialog.show(ft, "Set Alerts");
        } else if(type == REQUEST_MPLUS_POPUP) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            MPlusBadgePopupDialog dialog = new MPlusBadgePopupDialog();
            dialog.show(ft, "MPlus");
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

    @Override
    public String getOverviewText() {
        try {
            /*if (mSerpBackStack.peek() != null && mSerpBackStack.peek().selectedLocalitiesAndSuburbs() <= 1
                    && mListingGetEvent != null && mListingGetEvent.listingData != null && mListingGetEvent.listingData.facets != null) {
                return String.format("more about %s", mListingGetEvent.listingData.facets.buildDisplayName());
            }*/
            if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_SELLER) {
                return null;
            }
            ArrayList<SearchResponseItem> selectedSearches = getSelectedSearches();

            if(selectedSearches != null && selectedSearches.size() == 1) {
                if(SearchSuggestionType.CITY.getValue().equals(selectedSearches.get(0).type)) {
                    if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                        return String.format("more about %s", selectedSearches.get(0).city.toLowerCase());
                    }
                } else if(SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(selectedSearches.get(0).type)
                        || SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(selectedSearches.get(0).type)) {
                    return null;
                } else if(SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(selectedSearches.get(0).type)) {
                    return null;
                } else {
                    for(SearchResponseItem search : selectedSearches) {
                        if(!TextUtils.isEmpty(search.entityName)) {
                            if(SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(search.type)) {
                                return null;
                            } else {

                                /*if(!TextUtils.isEmpty(selectedSearches.get(0).city)) {
                                    return String.format("more about %s", String.format("%s, %s", search.entityName.toLowerCase(), selectedSearches.get(0).city.toLowerCase()));
                                } else {
                                    */return String.format("more about %s", search.displayText.toLowerCase());
                                //}
                            }
                        }
                    }
                }
            }

        }catch (Exception e){}
        return null;
    }

    @Override
    public void requestOverviewPage() {
        switch (mSerpRequestType & MASK_LISTING_TYPE) {
            case TYPE_CITY:
            case TYPE_GPID: {
                Intent cityIntent = new Intent(this, OverviewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putLong(OverviewActivity.ID, Long.valueOf(mSerpBackStack.peek().getCityId()));
                bundle.putInt(OverviewActivity.TYPE, OverviewItemType.CITY.ordinal());

                cityIntent.putExtras(bundle);
                startActivity(cityIntent);
                break;
            }
            case TYPE_LOCALITY:
            case TYPE_SUBURB:
            case TYPE_SEARCH: {
                Intent localityIntent = new Intent(this, OverviewActivity.class);
                Bundle bundle = new Bundle();

                localityIntent.putExtra(OverviewActivity.ID, Long.valueOf(mSerpBackStack.peek().getLocalityId()));
                bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

                localityIntent.putExtras(bundle);
                startActivity(localityIntent);
                break;
            }
            case TYPE_PROJECT: {
                Intent projectIntent = new Intent(this, OverviewActivity.class);
                Bundle bundle = new Bundle();

                bundle.putLong(OverviewActivity.ID, mSerpBackStack.peek().getProjectId());
                bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());

                projectIntent.putExtras(bundle);
                startActivity(projectIntent);
                break;
            }
        }
    }

    @Override
    public void trackScroll(int requestType, int position) {
        super.trackScroll(mSerpBackStack.peek(), requestType, position);
    }

    /*private void showProgress() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait while loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }*/

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        if(isActivityDead()){
            return;
        }

        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        if(isActivityDead()){
            return;
        }

        if(null==event.message){
            return;
        }
        if(null==mListings || mListings.isEmpty() || null==mListings.get(0)){
            return;
        }

        Listing listing = mListings.get(0);
        if(!TextUtils.isEmpty(listing.cityName)) {
            event.message.city = listing.cityName;
            displayPopupWindow(event.message);
        }
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
        return mListings != null;
    }

    private void setTrackUrl(){
        if((MASK_LISTING_TYPE &mSerpRequestType) == SerpActivity.TYPE_CLUSTER) {
            return;
        }

        if(null==mListings || mListings.isEmpty() || null==mListings.get(0)){
            return;
        }

        Listing listing = mListings.get(0);

        PageTag pageTag = new PageTag();
        pageTag.addCity(listing.cityName);

        ArrayList<SearchResponseItem> selectedSearches = getSelectedSearches();

        if (selectedSearches != null && selectedSearches.size() > 0) {
            if (SearchSuggestionType.SUBURB.getValue().equals(selectedSearches.get(0).type)
                    || SearchSuggestionType.LOCALITY.getValue().equals(selectedSearches.get(0).type)
                    || SearchSuggestionType.PROJECT.getValue().equals(selectedSearches.get(0).type)) {
                setLocalityName(listing.localityName);
                if (listing.localityId != null && listing.localityId > 0) {
                    setLocalityId(listing.localityId);
                } else if (listing.project != null && listing.project.locality != null && listing.project.locality.localityId != null) {
                    setLocalityId(listing.project.locality.localityId);
                }

                for(SearchResponseItem searchResponseItem : selectedSearches){
                    if(SearchSuggestionType.LOCALITY.getValue().equals(searchResponseItem.type)) {
                        pageTag.addLocality(searchResponseItem.entityName);
                    }else if(SearchSuggestionType.SUBURB.getValue().equals(searchResponseItem.type)) {
                        pageTag.addSuburb(searchResponseItem.entityName);
                    }else if(SearchSuggestionType.PROJECT.getValue().equals(searchResponseItem.type)) {
                        pageTag.addProject(searchResponseItem.entityName);
                        pageTag.addLocality(listing.localityName);
                    }else{
                        pageTag.addLocality(listing.localityName);
                    }
                }
            }
            setCityName(listing.cityName);
            if (listing.cityId != null) {
                setCityId(listing.cityId);
            }
        } else {
            pageTag.addLocality(listing.localityName);
        }

        super.setCurrentPageTag(pageTag);

    }

    protected boolean needJarvis() {
        return !mIsMapFragment;
    }

    @Override
    public void setFilterString(Properties properties) {
        this.mProperties =properties;
    }
}
