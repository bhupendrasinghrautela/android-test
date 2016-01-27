package com.makaan.activity.listing;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.fragment.listing.ChildSerpClusterFragment;
import com.makaan.fragment.listing.FiltersDialogFragment;
import com.makaan.fragment.listing.SerpListFragment;
import com.makaan.fragment.listing.SerpMapFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.request.selector.Selector;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.BuilderService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.listing.RelevancePopupWindowController;
import com.squareup.otto.Subscribe;

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

    public static final int TYPE_UNKNOWN = 0x00;
    public static final int TYPE_FILTER = 0x01;

    // for child serp
    public static final int TYPE_CLUSTER = 0x02;
    public static final int TYPE_SEARCH = 0x03;
    public static final int TYPE_SELLER = 0x04;
    public static final int TYPE_BUILDER = 0x05;

    public static final int MASK_LISTING_TYPE = 0x0f;
    public static final int MASK_LISTING_UPDATE_TYPE = 0xf0;

    public static final int TYPE_SORT = 0x10;
    public static final int TYPE_LOAD_MORE = 0x20;

    public static final int TYPE_SUGGESTION = 8;
    public static final int TYPE_CITY = 9;
    public static final int TYPE_PROJECT = 10;
    public static final int TYPE_LOCALITY = 11;

    // view requests to get new apis
    public static final int REQUEST_BUILDER_API = 0x01;
    public static final int REQUEST_SELLER_API = 0x02;

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

    @Override
    protected int getContentViewId() {
        return R.layout.activity_serp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int type = SerpActivity.TYPE_UNKNOWN;
        if(intent != null) {
            type = intent.getIntExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_UNKNOWN);
        }
        if(type == SerpActivity.TYPE_UNKNOWN) {
            // TODO check whether it should be used or not
            fetchData();
        } else {
            serpRequest(type, MakaanBuyerApplication.serpSelector);
        }

        // init fragments we need to use
        initUi(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MasterDataCache.getAppliedFilterCount() != 0) {
            mAppliedFiltersCountTextView.setVisibility(View.VISIBLE);
            mAppliedFiltersCountTextView.setText(String.valueOf(MasterDataCache.getAppliedFilterCount()));
        } else {
            mAppliedFiltersCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            setShowSearchBar(true);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);

            MakaanBuyerApplication.serpSelector.removePaging();
        } else if(mIsMapFragment) {
            mIsMapFragment = false;
        }
    }

    protected void initUi(boolean showSearchBar) {
        super.initUi(true);
    }

    private void fetchData() {
        MakaanBuyerApplication.serpSelector.term("cityId", "11").term("listingCategory", new String[]{"Primary"});
        serpRequest(SerpActivity.TYPE_UNKNOWN, MakaanBuyerApplication.serpSelector);
    }

    @Subscribe
    public synchronized void onResults(SerpGetEvent listingGetEvent) {
        if(mIsMapFragment && mMapFragment != null) {
            mListingGetEvent = listingGetEvent;
            mMapFragment.setData(mListingGetEvent.listingData);
        } else if (isSellerSerp) {
            setShowSearchBar(false);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
            if (mSellerSerpListFragment == null || !mSellerSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                childSerpClusterFragment.setData(mGroupListingGetEvent, this);
                // create new listing fragment to show the listings
                mSellerSerpListFragment = SerpListFragment.init(true);
                mSellerSerpListFragment.updateListings(listingGetEvent, null, this, (mSerpRequestType & MASK_LISTING_TYPE));

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                mSellerSerpListFragment.updateListings(listingGetEvent, null, this, (mSerpRequestType & MASK_LISTING_TYPE));
            }
        } else if ((mSerpRequestType & MASK_LISTING_TYPE) == SerpActivity.TYPE_CLUSTER) {
            setShowSearchBar(false);
            mFiltersFrameLayout.setVisibility(View.GONE);
            mSimilarPropertiesFrameLayout.setVisibility(View.VISIBLE);
            if (mChildSerpListFragment == null || !mChildSerpListFragment.isVisible()) {
                // create new child serp cluster fragment to show the cluster items
                ChildSerpClusterFragment childSerpClusterFragment = ChildSerpClusterFragment.init();
                childSerpClusterFragment.setData(mGroupListingGetEvent, this);
                // create new listing fragment to show the listings
                mChildSerpListFragment = SerpListFragment.init(true);
                mChildSerpListFragment.updateListings(listingGetEvent, null, this, (mSerpRequestType & MASK_LISTING_TYPE));

                initFragments(new int[]{R.id.activity_serp_similar_properties_frame_layout, R.id.activity_serp_content_frame_layout},
                        new Fragment[]{childSerpClusterFragment, mChildSerpListFragment}, true);

            } else {
                mChildSerpListFragment.updateListings(listingGetEvent, null, this, (mSerpRequestType & MASK_LISTING_TYPE));
            }
        } else {
            mListingGetEvent = listingGetEvent;
            // for buider, seller and normal serp
            if(!mGroupsRequested || mGroupReceived) {
                setShowSearchBar(true);
                mFiltersFrameLayout.setVisibility(View.VISIBLE);
                mSimilarPropertiesFrameLayout.setVisibility(View.GONE);
                // check if we have working listing fragment at this time
                if (mListingFragment == null) {
                    // create new listing fragment to show the listings
                    mListingFragment = SerpListFragment.init(false);
                    mListingFragment.updateListings(listingGetEvent, mGroupListingGetEvent, this, (mSerpRequestType & MASK_LISTING_TYPE));
                    initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
                } else {
                    // update already running listing fragment with the new list
                    mListingFragment.updateListings(listingGetEvent, mGroupListingGetEvent, this, (mSerpRequestType & MASK_LISTING_TYPE));
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
            setShowSearchBar(true);
            mFiltersFrameLayout.setVisibility(View.VISIBLE);
            mSimilarPropertiesFrameLayout.setVisibility(View.GONE);

            // check if we have working listing fragment at this time
            if (mListingFragment == null) {
                // create new listing fragment to show the listings
                mListingFragment = SerpListFragment.init(false);
                mListingFragment.updateListings(mListingGetEvent, mGroupListingGetEvent, this, (mSerpRequestType & MASK_LISTING_TYPE));
                initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, false);
            } else {
                // update already running listing fragment with the new list
                mListingFragment.updateListings(mListingGetEvent, mGroupListingGetEvent, this, (mSerpRequestType & MASK_LISTING_TYPE));
            }


            if(mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
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
                        MakaanBuyerApplication.serpSelector.sort(fieldName, value);
                        mSelectedSortIndex = i;
                        SerpActivity.this.serpRequest(SerpActivity.TYPE_SORT, MakaanBuyerApplication.serpSelector);
                    }
                }, mSelectedSortIndex);
        mMainFrameLayout.getForeground().setAlpha(128);
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }

    @OnClick(R.id.fragment_filters_map_image_view)
    public void onMapViewPressed(View view) {
        if(mIsMapFragment) {
            mListingFragment.updateListings(mListingGetEvent, mGroupListingGetEvent, this, (mSerpRequestType & MASK_LISTING_TYPE));
            initFragment(R.id.activity_serp_content_frame_layout, mListingFragment, true);
            mIsMapFragment = false;
            mMapImageView.setImageResource(R.drawable.map);
        } else {
            if (mListingGetEvent != null) {
                mMapFragment = new SerpMapFragment();
                mMapFragment.setData(mListingGetEvent.listingData);
                initFragment(R.id.activity_serp_content_frame_layout, mMapFragment, true);
                mIsMapFragment = true;
                mMapImageView.setImageResource(R.drawable.listed_by);
            }
        }
    }

    @Override
    public void dialogDismissed() {
        if(MasterDataCache.getAppliedFilterCount() != 0) {
            mAppliedFiltersCountTextView.setVisibility(View.VISIBLE);
            mAppliedFiltersCountTextView.setText(String.valueOf(MasterDataCache.getAppliedFilterCount()));
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
        if((type & MASK_LISTING_TYPE) > 0) {
            mSerpRequestType = type;
        } else {
            mSerpRequestType = (mSerpRequestType & MASK_LISTING_TYPE) | type;
        }

        if(type != TYPE_LOAD_MORE) {
            if(type != TYPE_SORT) {
                mSelectedSortIndex = 0;
                MakaanBuyerApplication.serpSelector.sort("price", "desc");
                mSortTextView.setText("price");
            }
            showProgress();
        }

        // remove paging if we are loading new serp results
        if((mSerpRequestType & MASK_LISTING_UPDATE_TYPE) == 0) {
            selector.removePaging();
        }

        if((mSerpRequestType & MASK_LISTING_TYPE) == TYPE_CLUSTER) {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                    .handleChildSerpRequest(MakaanBuyerApplication.serpSelector, mChildListingId);
            mGroupsRequested = false;
            mGroupReceived = true;
            mSerpReceived = false;
        } else {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).handleSerpRequest(selector, true);
            mGroupsRequested = true;
            mSerpReceived = false;
            mGroupReceived = false;
        }
    }

    @Override
    public void serpRequest(int type, Long id) {
        mChildListingId = id;
        if((type & MASK_LISTING_TYPE) > 0) {
            mSerpRequestType = type;
        } else {
            mSerpRequestType = (mSerpRequestType & MASK_LISTING_TYPE) | type;
        }
        if(mSerpRequestType == TYPE_CLUSTER) {
            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class))
                    .handleChildSerpRequest(MakaanBuyerApplication.serpSelector, id);
            mGroupsRequested = false;
            mGroupReceived = true;
            mSerpReceived = false;
        }
        showProgress();
    }

    @Override
    public void requestApi(int request, String key) {
        if(request == REQUEST_BUILDER_API) {
            HashSet<String> ids = MakaanBuyerApplication.serpSelector.getTerm(key);
            if(ids.size() == 1) {
                ((BuilderService)MakaanServiceFactory.getInstance().getService(BuilderService.class))
                        .getBuilderById(Long.valueOf((String)(ids.toArray()[0])));
            }
        }
    }

    private void showProgress() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait while loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event){
        displayPopupWindow(event.message);
    }
}
