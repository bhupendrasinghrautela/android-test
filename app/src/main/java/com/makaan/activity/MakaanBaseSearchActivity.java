package com.makaan.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationListener;
import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerJourneyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.adapter.listing.SearchAdapter;
import com.makaan.adapter.listing.SelectedSearchAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.cookie.Session;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.location.LocationServiceConnectionListener;
import com.makaan.location.MakaanLocationManager;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.search.SearchResponseHelper;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.user.UserResponse;
import com.makaan.service.LocationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SearchService;
import com.makaan.ui.listing.CustomFlowLayout;
import com.makaan.util.CommonUtil;
import com.makaan.util.ErrorUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.PermissionManager;
import com.makaan.util.RecentSearchManager;
import com.segment.analytics.Properties;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rohitgarg on 1/10/16.
 */
public abstract class MakaanBaseSearchActivity extends MakaanFragmentActivity implements
        SearchAdapter.SearchAdapterCallbacks, TextWatcher, CustomFlowLayout.ItemRemoveListener, LocationListener {

    public static final int SERP_CONTEXT_BUY = 1;
    public static final int SERP_CONTEXT_RENT = 2;

    protected static final int HIDE_THRESHOLD = 50;

    protected int scrolledDistance;
    protected boolean controlsVisible = true;

    // current context of serp, SERP_CONTEXT_BUY or SERP_CONTEXT_RENT
    protected int mSerpContext = SERP_CONTEXT_BUY;

    @Bind(R.id.activity_search_base_search_frame_layout)
    FrameLayout mSearchLayoutFrameLayout;
    @Bind(R.id.activity_search_base_search_results_frame_layout)
    FrameLayout mSearchResultFrameLayout;

    @Bind(R.id.activity_search_toolbar_profile_icon_image_view)
    CircleImageView mImageViewBuyer;
    @Bind(R.id.activity_search_toolbar_profile_icon_text_view)
    TextView  mTextViewBuyerInitials;

    @Bind(R.id.activity_search_base_layout_search_bar_search_image_view)
    ImageView mSearchImageView;
    @Bind(R.id.activity_search_base_search_no_result_image_view)
    ImageView mNoResultsImageView;

    @Bind(R.id.activity_search_base_search_no_result_layout)
    View mNoResultLayout;

    @Bind(R.id.activity_search_base_search_loading_progress_bar)
    ImageView mLoadingProgressBar;

    @Bind(R.id.activity_search_base_layout_search_bar_search_image_button)
    ImageButton mSearchImageButton;
    @Bind(R.id.activity_search_base_layout_search_bar_search_close_button)
    ImageButton mDeleteButton;

    @Bind(R.id.activity_search_base_layout_search_bar_search_text_view)
    TextView mSearchPropertiesTextView;
    @Bind(R.id.activity_search_base_search_no_result_text_view)
    TextView mNoResultsTextView;

    @Bind(R.id.fragment_search_results_recycler_view)
    RecyclerView mSearchRecyclerView;

    @Bind(R.id.activity_search_base_layout_search_bar_description_relative_view)
    RelativeLayout mSearchDescriptionRelativeView;
    @Bind(R.id.activity_search_base_layout_search_bar_search_relative_view)
    RelativeLayout mSearchRelativeView;

    @Bind(R.id.activity_search_base_layout_search_bar_search_edit_text)
    protected EditText mSearchEditText;

    @Bind(R.id.activity_search_base_layout_search_bar_back_button)
    Button mBackButton;

    @Bind(R.id.fragment_search_results_wrap_layout)
    CustomFlowLayout mSearchResultsFlowLayout;

    FrameLayout mContentFrameLayout;
    private SearchAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<SearchResponseItem> mSearches;
    protected FrameLayout mMainFrameLayout;
    private int mSearchImageViewX;
    private int mSearchImageButtonX;

    private ArrayList<SearchResponseItem> mSelectedSearches = new ArrayList<>();
    private SelectedSearchAdapter mSelectedSearchAdapter;
    private int mMaxSearchClubCount;
    private ArrayList<SearchResponseItem> mAvailableSearches = new ArrayList<>();
    private boolean mSearchResultReceived;
    private MakaanLocationManager mMakaanLocationManager;
    private LocationManager mLocationManager;
    private boolean mSearchEditTextVisible;
    private boolean mNearByLocalitiesClicked;

    @Override
    protected abstract int getContentViewId();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_search_base);
        mContentFrameLayout = (FrameLayout) findViewById(R.id.activity_search_base_content_frame_layout);

        View view = LayoutInflater.from(this).inflate(layoutResID, mContentFrameLayout, false);

        // dim 0 for the window
        mContentFrameLayout.addView(view);

        mMainFrameLayout = (FrameLayout) findViewById(R.id.activity_search_base_frame_layout);
        mMainFrameLayout.getForeground().setAlpha(0);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mSearchImageViewX = size.x - (this.getResources().getDimensionPixelSize(R.dimen.back_button_width)
                + this.getResources().getDimensionPixelSize(R.dimen.back_button_margin_left)
                + this.getResources().getDimensionPixelSize(R.dimen.back_button_margin_right)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_text_view_margin_left)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_width)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_margin_right));


        mSearchImageButtonX = this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_width)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_margin_right)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_builder_image_view_width)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_builder_image_view_margin_right)
                - this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_width)
                - this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_margin_right);

        mMaxSearchClubCount = this.getResources().getInteger(R.integer.max_search_count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSearchBarCollapsible(needScrollableSearchBar());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserData();
        if(this instanceof HomeActivity) {
            if (getLocationAvailabilty()) {
                connectLocationApiClient(MakaanLocationManager.LocationUpdateMode.ONCE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            stopLocationUpdate(this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            startLocationUpdate();
        } catch(Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onDestroy() {

        disconnectLocationApiClient();
        super.onDestroy();
    }

    /**
     * Starts location update
     * */
    protected void startLocationUpdate() {
        if(mMakaanLocationManager != null) {
            mMakaanLocationManager.requestLocationUpdate();
        }
    }


    /**
     * Stops location update
     * @param listener a location listener
     * */
    protected void stopLocationUpdate(LocationListener listener){
        if(mMakaanLocationManager != null) {
            mMakaanLocationManager.stopLocationUpdate(listener);
        }
    }

    /*
    disconnect the Location client
     */
    protected void disconnectLocationApiClient() {
        if (mMakaanLocationManager != null) {
            mMakaanLocationManager.disconnectLocationApiClient(this);
        }
    }

    public boolean getLocationAvailabilty() {
        if(!PermissionManager.isPermissionRequestRequired(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } else {
            return false;
        }
    }

    /**
     * starts location update
     * @param mode location update frequency mode
     * */
    protected void connectLocationApiClient(MakaanLocationManager.LocationUpdateMode mode) {
        if(mMakaanLocationManager == null){
            mMakaanLocationManager = new MakaanLocationManager();
        }
        LocationServiceConnectionListener listener =
                new LocationServiceConnectionListener(this, mMakaanLocationManager);

        mMakaanLocationManager.connectLocationApiClient(this, listener, this, mode);
        Session.locationRequested = true;
        if(mSearchResultFrameLayout.getVisibility() == View.VISIBLE && mSearchAdapter != null) {
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Session.phoneLocation = location;
        if(mMakaanLocationManager.getLocationUpdateMode()
                == MakaanLocationManager.LocationUpdateMode.ONCE) {
            try {
                stopLocationUpdate(this);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        if(mSearchResultFrameLayout.getVisibility() == View.VISIBLE && mSearchAdapter != null) {
            mSearchAdapter.notifyDataSetChanged();
            Session.locationRequested = false;
        }
        if(mNearByLocalitiesClicked) {
            SearchResponseItem item = new SearchResponseItem();
            item.type = SearchSuggestionType.NEARBY_PROPERTIES.getValue();
            item.displayText = "properties near my location";
            if(Session.phoneLocation != null) {
                item.latitude = Session.phoneLocation.getLatitude();
                item.longitude = Session.phoneLocation.getLongitude();
                onSearchItemClick(item);
            }
        }
    }

    private void initializeViewData() {

        mSearchAdapter = new SearchAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);

        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        mSelectedSearchAdapter = new SelectedSearchAdapter(this);
        mSearchResultsFlowLayout.setAdapter(mSelectedSearchAdapter);
        mSelectedSearchAdapter.setItemRemoveListener(this);
    }

    /**
     * must call this function from onCreate function by child activity
     * with default preference of whether search bar should show or not
     *
     * @param showSearchBar boolean to control whether search bar should show or not
     */
    protected void initUi(boolean showSearchBar) {
        setShowSearchBar(showSearchBar, false);

        /*int id = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(Color.WHITE);*/

        /*int searchHintIconId = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchHintIcon = (ImageView) mSearchView.findViewById(searchHintIconId);
        searchHintIcon.setImageResource(R.drawable.search_white);
        searchHintIcon.setMaxWidth(this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_width));
        searchHintIcon.setMaxHeight(this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_search_image_button_height));*/

        mSearchEditText.addTextChangedListener(this);

        // initialize adapter and manager for the recycler view and flow layout
        initializeViewData();
    }

    protected void setShowSearchBar(boolean showSearchBar, boolean animate) {
        if(!animate) {
            if (showSearchBar) {
                mSearchLayoutFrameLayout.setVisibility(View.VISIBLE);
            } else {
                mSearchLayoutFrameLayout.setVisibility(View.GONE);
                showContent();
            }
        } else {
            if(showSearchBar) {
                ((ViewGroup)mSearchLayoutFrameLayout.getParent()).setTranslationY(-mSearchLayoutFrameLayout.getHeight());
                mSearchLayoutFrameLayout.setVisibility(View.VISIBLE);
                ((ViewGroup)mSearchLayoutFrameLayout.getParent()).animate()
                        .translationY(0).setInterpolator(new DecelerateInterpolator(2))
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                });
            } else {
                ((ViewGroup)mSearchLayoutFrameLayout.getParent()).animate()
                        .translationY(-mSearchLayoutFrameLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2))
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(mSearchLayoutFrameLayout != null) {
                                    mSearchLayoutFrameLayout.setVisibility(View.GONE);
                                    ((ViewGroup) mSearchLayoutFrameLayout.getParent()).setTranslationY(0);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        }
    }

    protected String childScreenName() {
        return null;
    }

    @Override
    public void onSearchItemClick(SearchResponseItem searchResponseItem) {

        if(mSerpContext==SERP_CONTEXT_BUY){

            switch(getScreenName().toLowerCase()){
                case ScreenNameConstants.SCREEN_NAME_HOME:{

                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeBuy);
                    Properties property = MakaanEventPayload.beginBatch();

                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    property.put(MakaanEventPayload.LABEL, String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeBuyLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeBuyRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_PROJECT:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectBuy);
                    Properties property = MakaanEventPayload.beginBatch();

                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    property.put(MakaanEventPayload.LABEL, String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectBuyLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectBuyRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyBuy);
                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    property.put(MakaanEventPayload.LABEL, String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyBuyLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyBuyRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_SERP:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpBuy);
                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    property.put(MakaanEventPayload.LABEL, String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpBuyLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpBuyRank);
                    String s = childScreenName();
                    break;
                }
            }


        }
        else if(mSerpContext==SERP_CONTEXT_RENT)  {

            switch(getScreenName().toLowerCase()){
                case ScreenNameConstants.SCREEN_NAME_HOME:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeRent);
                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    property.put(MakaanEventPayload.LABEL,String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeRentLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchHomeRentRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_PROJECT:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));

                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectRent);
                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    property.put(MakaanEventPayload.LABEL,String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectRentLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchProjectRentRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyRent);

                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    property.put(MakaanEventPayload.LABEL,String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyRentLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchPropertyRentRank);
                    break;
                }
                case ScreenNameConstants.SCREEN_NAME_SERP:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.KEYWORD, mSearchEditText.getText().toString());
                    properties.put(MakaanEventPayload.CHARACTERS_LENGTH, String.valueOf(mSearchEditText.getText().toString().length()));
                    properties.put(MakaanEventPayload.LABEL,
                            properties.get(MakaanEventPayload.LABEL) + "_" + mSearchEditText.getText().toString());
                    String rank=(String.valueOf(properties.get(MakaanEventPayload.SUGGESTION_POSITION)));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpRent);

                    Properties property = MakaanEventPayload.beginBatch();
                    property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    property.put(MakaanEventPayload.LABEL,String.valueOf(mSearchEditText.getText().toString().length()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpRentLength);

                    Properties propert = MakaanEventPayload.beginBatch();
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    propert.put(MakaanEventPayload.LABEL,rank);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.searchSerpRentRank);
                    break;
                }

            }

        }

        if(SearchSuggestionType.NEARBY_PROPERTIES.getValue().equalsIgnoreCase(searchResponseItem.type)) {
            // if we don't have phone location
            if(Session.phoneLocation == null) {
                // check we need to request permission for gps
                if (PermissionManager.isPermissionRequestRequired(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // if yes, then request for permission and cancel current search request
                    PermissionManager.begin().addRequest(PermissionManager.FINE_LOCATION_REQUEST).request(this);
                    mNearByLocalitiesClicked = true;
                    return;
                } else if(!getLocationAvailabilty()) {
                    mNearByLocalitiesClicked = true;
                    // location provider is not enabled
                    MakaanMessageDialogFragment.showMessage(getFragmentManager(), "please enable location provider to use this option", "ok",
                            new MakaanMessageDialogFragment.MessageDialogCallbacks() {
                        @Override
                        public void onPositiveClicked() {
                            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }

                        @Override
                        public void onNegativeClicked() {

                        }
                    });
                    return;
                } else if(Session.phoneLocation == null/* && Session.apiLocation == null*/) {
                    // if we don't need to request permission for gps
                    // and api location is also not available, then reject
                    mNearByLocalitiesClicked = true;
                    if(getLocationAvailabilty()) {
                        connectLocationApiClient(MakaanLocationManager.LocationUpdateMode.ONCE);
                    }
                    // todo show some message
                    return;
                }
            }
        }
        mNearByLocalitiesClicked = false;
        showContent();

        if(supportsListing()  && (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchResponseItem.type)
                || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchResponseItem.type))) {
            addSearchInWrapLayout(searchResponseItem);

        } else {
            // if selected search is not locality, then clear the selected search array list
            mSelectedSearches.clear();

            // set null as data for adapter of wrap layout
            mSelectedSearchAdapter.setData(null);

            // set hint of edit text accordingly and add current item in selected search
            if(mSerpContext == SERP_CONTEXT_BUY) {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_default_hint));
            } else {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_default_rent_hint));
            }
            mSelectedSearches.add(searchResponseItem);
        }

        handleSearch();

        if(!supportsListing() || SearchSuggestionType.NEARBY_PROPERTIES.getValue().equals(searchResponseItem.type)) {
            mSelectedSearches.clear();
        }

        if(!SearchSuggestionType.NEARBY_PROPERTIES.getValue().equals(searchResponseItem.type)) {
            // add selected search to recent searches
            RecentSearchManager.getInstance(getApplicationContext()).addEntryToRecentSearch(searchResponseItem, this);
        }

        /*if(!areListingsAvailable() && (SearchSuggestionType.CITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)
                || SearchSuggestionType.LOCALITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)
                || SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchResponseItem.type))) {
            finish();
        }*/

        /*if(supportsListing() && !SearchSuggestionType.NEARBY_PROPERTIES.getValue().equals(searchResponseItem.type)) {
            setTitle(searchResponseItem.displayText);
        }*/
    }

    private void addSearchInWrapLayout(SearchResponseItem searchResponseItem) {
        // if adapter doesn't have any item, then it means previous search was either project, city, builder etc
        // or there is no earlier search present, so just clear selected search array
        if(mSelectedSearchAdapter.getItemCount() == 0) {
            mSelectedSearches.clear();
        }
        // add selected search result to arraylist
        mSelectedSearches.add(searchResponseItem);
        // as it is locality, show it in the selected search wrap view
        mSelectedSearchAdapter.setData(mSelectedSearches);

        // if there is still room for more localities to be added, then show hint accordingly
        // or disable the edit text
        if(mSelectedSearches.size() < mMaxSearchClubCount) {
            mSearchEditText.setHint(this.getResources().getString(R.string.search_locality_hint));
        } else {
            mSearchEditText.setHint("");
            mSearchEditText.setEnabled(false);
        }
    }

    private void handleSearch() {

        /*Analytics.with(this).track(MakaanTrackerConstants.Action.searchPropertyBuy.getValue(), new Properties()
                .putValue("Category", MakaanTrackerConstants.Category.property.getValue())
                .putValue(MakaanTrackerConstants.KEYWORD, mSearchEditText.getText().toString())
                .putValue(MakaanTrackerConstants.LISTING_POSITION, 3));

        Analytics.with(this).flush();*/

        // hide the keypad
        showKeypad(mSearchEditText, false);

        SearchResponseHelper.resolveSearch(mSelectedSearches, this);

        // clear the search adapter to show empty results
        mSearchAdapter.clear();

        // clear search edittext
        mSearchEditText.removeTextChangedListener(this);
        mSearchEditText.setText("");
        mSearchEditText.addTextChangedListener(this);

        // hide search view and show default
        setSearchViewVisibility(false);
    }

    private void showKeypad(View view, boolean show) {
        if(show) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 300);
        } else {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @OnClick(R.id.activity_search_base_layout_search_bar_back_button)
    public void onUpPressed(View view) {
        switch(getScreenName().toLowerCase()){
            case ScreenNameConstants.SCREEN_NAME_HOME:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProject);
                break;
            }
            case ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProperty);
                break;
            }
            case ScreenNameConstants.SCREEN_NAME_SERP:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerp);
                break;
            }


        }
//        onBackPressed();
        if(this instanceof SerpActivity) {
            finish();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        // change search view visibility to gone if it is visible
        if(mSearchRelativeView.getVisibility() == View.VISIBLE) {
            setSearchViewVisibility(false);
            // hide keypad
            showKeypad(mSearchEditText, false);
            /*if(!areListingsAvailable()) {
                super.onBackPressed();
            }*/
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected boolean needBackProcessing() {
        boolean visible = mSearchRelativeView.getVisibility() == View.VISIBLE;
        if(visible) {
            return true;
        } else {
            return super.needBackProcessing();
        }
    }

    protected boolean areListingsAvailable() {
        return false;
    }

    @OnClick({R.id.activity_search_base_layout_search_bar_search_image_button,
            R.id.activity_search_base_layout_search_bar_search_text_view,
            R.id.activity_search_base_layout_search_bar_description_relative_view})
    public void onSearchPressed(View view) {
        setSearchViewVisibility(true);
    }

    protected void setSearchViewVisibility(boolean searchViewVisible) {

        /*if (!searchViewVisible && searchPropertiesText != null && !TextUtils.isEmpty(searchPropertiesText)) {
            mSearchPropertiesTextView.setText(searchPropertiesText);
        }*/
        // if search view is requested to be shown, then show recent searches
        if(searchViewVisible) {
            showEmptySearchResults();
        }

        // if visibility sent is false, then hide search result frame layout
        if(!searchViewVisible) {
            if (mSearchLayoutFrameLayout != null) {
                showContent();
            }
        }

        // show keypad id we need to show search view
        if(searchViewVisible) {
            if(mSelectedSearches.size() >= 0 && mSelectedSearches.size() < mMaxSearchClubCount) {
                showKeypad(mSearchEditText, true);
            }
        }

        // make sure search bar is not  collapsing on scrolling when search results are available
        setSearchBarCollapsible(searchViewVisible ? false : needScrollableSearchBar());

        // show animation for the glass icon according to visibility sent
        if(searchViewVisible) {
            mSearchDescriptionRelativeView.setVisibility(View.GONE);
            mSearchEditTextVisible = true;
            mSearchRelativeView.setVisibility(View.VISIBLE);
            mSearchEditText.requestFocus();
            if(TextUtils.isEmpty(mSearchEditText.getText().toString())) {
                final CharSequence hintText = mSearchEditText.getHint();
                mSearchEditText.setHint("");
                mDeleteButton.setBackgroundResource(R.drawable.search_white);
                mDeleteButton.setVisibility(View.GONE);

                mSearchImageView.setVisibility(View.VISIBLE);

                mSearchImageView.setTranslationX(-mSearchImageViewX);

                mSearchImageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("translationX", 0);

                        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mSearchImageView, propx);
                        animator.setDuration(400);
                        animator.start();
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(mDeleteButton != null) {
                                    mDeleteButton.setVisibility(View.VISIBLE);
                                }
                                if(mSearchImageView != null) {
                                    mSearchImageView.setVisibility(View.GONE);
                                }
                                if(hintText != null && mSearchEditText != null) {
                                    mSearchEditText.setHint(hintText);
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                }, 800);
            } else {
                mSearchEditTextVisible = true;
                mDeleteButton.setBackgroundResource(R.drawable.close_white);
                mSearchImageView.setVisibility(View.GONE);
            }
        } else {
            if(TextUtils.isEmpty(mSearchEditText.getText().toString())) {
                mSearchImageView.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.GONE);
                mSearchEditTextVisible = false;
                mSearchImageView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PropertyValuesHolder propx = PropertyValuesHolder.ofFloat("translationX", -mSearchImageButtonX);

                        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mSearchImageView, propx);
                        animator.setDuration(400);
                        animator.start();
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(!mSearchEditTextVisible) {
                                    if (mSearchDescriptionRelativeView != null) {
                                        mSearchDescriptionRelativeView.setVisibility(View.VISIBLE);
                                    }
                                    if (mSearchRelativeView != null) {
                                        mSearchRelativeView.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                }, 800);
            } else {
                mSearchDescriptionRelativeView.setVisibility(View.VISIBLE);
                mSearchEditTextVisible = false;
                mSearchRelativeView.setVisibility(View.GONE);
            }
        }

        if(!searchViewVisible && this instanceof SerpActivity) {
            setJarvisVisibility(needJarvis());
        } else {
            setJarvisVisibility(!searchViewVisible);
        }
    }

    protected boolean needJarvis() {
        return true;
    }

    private void showEmptySearchResults() {
        if(mSelectedSearches.size() > 0 && (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(mSelectedSearches.get(0).type)
                || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(mSelectedSearches.get(0).type))) {
            if(mSelectedSearches.size() < mMaxSearchClubCount) {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_locality_hint));
                LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
                service.getTopNearbyLocalitiesAsSearchResult(mSelectedSearches.get(mSelectedSearches.size() - 1));
                showSearchResults();
            } else {
                mSearchEditText.setHint("");
                mSearchEditText.setEnabled(false);
                showSearchResults();
            }
        } else {
            mSearchEditText.setEnabled(true);
            if(mSerpContext == SERP_CONTEXT_BUY) {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_default_hint));
            } else {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_default_rent_hint));
            }
            ArrayList<SearchResponseItem> searches = RecentSearchManager.getInstance(getApplicationContext()).getRecentSearches(this);
            if (searches != null && searches.size() > 0) {

                mSearchResultReceived = false;
                showSearchResults();
                if(mSearches == null) {
                    mSearches = new ArrayList<>();
                } else {
                    mSearches.clear();
                }
                mSearches.addAll(searches);

                // add header text
                SearchResponseItem item = new SearchResponseItem();
                item.type = SearchSuggestionType.HEADER_TEXT.getValue();
                item.displayText = "recent searches";
                mSearches.add(0, item);

                clearSelectedSearches();
                addNearbyPropertiesSearchItem();
                mSearchAdapter.setData(mAvailableSearches, true);
            } else {
                //if(this instanceof HomeActivity) {
                    // check if we have user's location
                    if(Session.phoneLocation != null) {
                        LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
                        service.getTopLocalitiesAsSearchResult(Session.phoneLocation.getLatitude(), Session.phoneLocation.getLongitude(), 0);
                    } else if(Session.apiLocation != null && Session.apiLocation.centerLatitude != null
                            && Session.apiLocation.centerLongitude != null) {
                        LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
                        service.getTopLocalitiesAsSearchResult(Session.apiLocation.centerLatitude, Session.apiLocation.centerLongitude, Session.apiLocation.id);
                    }
                    showSearchResults();
                /*} else {
                    clearSelectedSearches();
                    addNearbyPropertiesSearchItem();
                    // we need empty layout even when no search results are present
                    mSearchAdapter.setData(mAvailableSearches, false);
                    showSearchResults();
                }*/
            }
        }
    }

    private void addNearbyPropertiesSearchItem() {
//        if(Session.apiLocation != null || Session.phoneLocation != null) {
            SearchResponseItem item = new SearchResponseItem();
            item.type = SearchSuggestionType.NEARBY_PROPERTIES.getValue();
            item.displayText = "properties near my location";
            if(Session.phoneLocation != null) {
                item.latitude = Session.phoneLocation.getLatitude();
                item.longitude = Session.phoneLocation.getLongitude();
            }/* else if(Session.apiLocation != null) {
                item.latitude = Session.apiLocation.centerLatitude;
                item.longitude = Session.apiLocation.centerLongitude;
            }*/
            mAvailableSearches.add(0, item);
//        }
    }

    private void addErrorSearchItem(String message) {
        SearchResponseItem item = new SearchResponseItem();
        item.type = SearchSuggestionType.ERROR.getValue();
        if(TextUtils.isEmpty(message)) {
            item.displayText = this.getResources().getString(R.string.generic_error);
        } else {
            item.displayText = message;
        }
        mAvailableSearches.clear();
        mAvailableSearches.add(0, item);
    }

    /*private void setSearchResultFrameLayoutVisibility(boolean visible) {
        mSearchResultFrameLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(visible) {
            if (mSelectedSearchAdapter.getItemCount() > 0) {
                mSearchResultsFlowLayout.setVisibility(View.VISIBLE);
            } else {
                mSearchResultsFlowLayout.setVisibility(View.GONE);
            }
        }
    }*/

    public void onResults(SearchResultEvent searchResultEvent) {
        if(isActivityDead()){
            return;
        }


        if (null == searchResultEvent || null == searchResultEvent.searchResponse || null != searchResultEvent.error) {
            if(searchResultEvent.error != null && !TextUtils.isEmpty(searchResultEvent.error.msg)) {
                addErrorSearchItem(searchResultEvent.error.msg);
            } else {
                addErrorSearchItem(this.getResources().getString(R.string.generic_error));
            }
            mSearchAdapter.setData(mAvailableSearches, true);
            return;
        }
        
        if(mSearchResultFrameLayout.getVisibility() == View.VISIBLE) {

            mSearchResultReceived = true;
            showSearchResults();
            this.mSearches = searchResultEvent.searchResponse.getData();

            // todo remove any search result which we cannot handle
            if(mSearches != null) {
                for (Iterator<SearchResponseItem> iterator = mSearches.iterator(); iterator.hasNext(); ) {
                    SearchResponseItem search = iterator.next();
                    if (search == null) {
                        iterator.remove();
                    } else {
                        try {
                            SearchResponseHelper.getType(search);
                        } catch (Exception ex) {
                            iterator.remove();
                            if(search.type != null) {
                                Crashlytics.log(search.type);
                            }
                            Crashlytics.logException(ex);
                        }
                    }
                }
            }

            clearSelectedSearches();

            /* track code [start]*/
            if(this.mSearches.size() == 0){
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorUsability);
                properties.put(MakaanEventPayload.LABEL, mSearchEditText.getText());
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.sorryNoMatchingResultFound);

            }
            /* track code [end]*/

            if(TextUtils.isEmpty(mSearchEditText.getText())) {
                addNearbyPropertiesSearchItem();
            } else if(this.mSearches.size() == 0) {
                addErrorSearchItem(this.getResources().getString(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, false)));
            }
            mSearchAdapter.setData(mAvailableSearches, false);
        }
    }

    private void clearSelectedSearches() {
        mAvailableSearches.clear();
        if(mSearches != null) {
            mAvailableSearches.addAll(mSearches);
        }
        if(mSelectedSearches.size() > 0) {
            if(SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(mSelectedSearches.get(0).type)
                    || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(mSelectedSearches.get(0).type)) {
                HashSet<String> searches = new HashSet<>();
                for(SearchResponseItem search : mSelectedSearches) {
                    searches.add(search.id);
                }

                for (Iterator<SearchResponseItem> iterator = mAvailableSearches.iterator(); iterator.hasNext();) {
                    SearchResponseItem search = iterator.next();
                    if (!searches.add(search.id)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable != null && !TextUtils.isEmpty(editable.toString())) {
            SearchService service = (SearchService) MakaanServiceFactory.getInstance().getService(SearchService.class);
            try {
//                Log.d("DEBUG", editable.toString());
                // if selected search adapter has items already, then it means there is some locality already selected
                // so we need to search for localities only in the selected city
                if(mSelectedSearchAdapter != null) {
                    if (mSelectedSearchAdapter.getItemCount() > 0) {
                        if(mSelectedSearches != null) {
                            service.getSearchResults(editable.toString(), (mSerpContext == SERP_CONTEXT_BUY ? "buy" : "rent"),
                                    mSelectedSearches.get(0).city, new SearchType[]{SearchType.LOCALITY, SearchType.SUBURB}, false);
                        }
                    } else {
                        // search for everything everywhere
                        service.getSearchResults(editable.toString(), (mSerpContext == SERP_CONTEXT_BUY ? "buy" : "rent"),
                                null, SearchType.ALL, true);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                CommonUtil.TLog("exception", e);
                Crashlytics.logException(e);
            }
            // handle visibility of delete/cross button
            mDeleteButton.setBackgroundResource(R.drawable.close_white);
        } else {
//            mSearchResultFrameLayout.setVisibility(View.GONE);
            // show recent searches as there is no text in the search view
            if(mSearches != null) {
                mSearches.clear();
            }
            showEmptySearchResults();

            mDeleteButton.setBackgroundResource(R.drawable.search_white);
        }
    }
    @OnClick(R.id.activity_search_base_layout_search_bar_search_close_button)
    public void onDeletePressed(View view) {
        if(TextUtils.isEmpty(mSearchEditText.getText().toString())) {
            if(mSelectedSearches.size() > 0) {
                handleSearch();
            }
        } else {
            mSearchEditText.setText("");
        }
    }

    protected void openSearch(boolean open) {
        setSearchViewVisibility(open);
    }

    @Override
    public void itemRemoved(Object item) {
        if(mSelectedSearches != null && mSelectedSearches.size() > 0
                && item != null && item instanceof SearchResponseItem) {
            // we need to remove the item from saved searches
            mSelectedSearches.remove(item);
            // enable search edit text
            mSearchEditText.setEnabled(true);

            // show search edittext hint based on current no of selected searches
            mSearchEditText.setEnabled(true);
            if(mSelectedSearches.size() > 0) {
                mSearchEditText.setHint(this.getResources().getString(R.string.search_locality_hint));
            } else {
                mSelectedSearchAdapter.setData(null);
                if(mSerpContext == SERP_CONTEXT_BUY) {
                    mSearchEditText.setHint(this.getResources().getString(R.string.search_default_hint));
                } else {
                    mSearchEditText.setHint(this.getResources().getString(R.string.search_default_rent_hint));
                }
            }

            showEmptySearchResults();
        }
    }

    protected ArrayList<SearchResponseItem> getSelectedSearches() {
        ArrayList<SearchResponseItem> list = new ArrayList<>();
        list.addAll(mSelectedSearches);
        return list;
    }


    protected int getSearchBarHeight() {
        return mSearchLayoutFrameLayout.getHeight();
    }

    @OnEditorAction(R.id.activity_search_base_layout_search_bar_search_edit_text)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH) {
            if(mSearchAdapter != null) {
                mSearchAdapter.onSearchPressed();
                return true;
            }
        }
        return false;
    }

    /**
     * open buyer journey activity
     */
    @OnClick(R.id.activity_search_toolbar_profile_icon)
    public void click(){
//        Log.e("screen ","name"+getScreenName());
        switch(getScreenName().toLowerCase()){
            case ScreenNameConstants.SCREEN_NAME_PROJECT:{
                Properties properties= MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.myAccount);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProject);
                break;
            }
            case ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.myAccount);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProperty);
                break;
            }
            case ScreenNameConstants.SCREEN_NAME_SERP:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.myAccount);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerp);
                break;
            }
        }
        Intent intent=new Intent(MakaanBaseSearchActivity.this,BuyerJourneyActivity.class);
        intent.putExtra("screenName", getScreenName());
        startActivity(intent);
    }

    /**
     * set title of the activity
     * @param title title to display in the toolbar*
     */
    protected void setTitle(String title) {
        if(!TextUtils.isEmpty(title)) {
            mSearchPropertiesTextView.setText(title.toLowerCase());
        } else {
            mSearchPropertiesTextView.setText("");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if(!TextUtils.isEmpty(title)) {
            mSearchPropertiesTextView.setText(title.toString().toLowerCase());
        } else {
            mSearchPropertiesTextView.setText("");
        }
    }

    protected void setSearchBarCollapsible(boolean isCollapsible) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mSearchLayoutFrameLayout.getLayoutParams();
        if(isCollapsible) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        } else {
            params.setScrollFlags(0);
        }
        mSearchLayoutFrameLayout.setLayoutParams(params);
        if(!isCollapsible) {
            ((AppBarLayout) findViewById(R.id.main_appbar)).setExpanded(true);
        }
    }

    protected void applySearch(ArrayList<SearchResponseItem> searches) {
        if(mSelectedSearches == null) {
            mSelectedSearches = new ArrayList<>();
        }
        if(mSelectedSearchAdapter == null) {
            mSelectedSearchAdapter = new SelectedSearchAdapter(this);
            mSearchResultsFlowLayout.setAdapter(mSelectedSearchAdapter);
            mSelectedSearchAdapter.setItemRemoveListener(this);
        }

        mSelectedSearches.clear();
        mSelectedSearchAdapter.setData(null);
        for(SearchResponseItem item : searches) {
            if(SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(item.type)
                    || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(item.type)) {
                addSearchInWrapLayout(item);
            } else {
                mSelectedSearches.add(item);
            }
        }
    }

    protected abstract boolean needScrollableSearchBar();
    protected abstract boolean supportsListing();

    private void setUserData() {
        UserResponse info= CookiePreferences.getUserInfo(this);
        if(null!=info && null!=info.getData()) {
            mImageViewBuyer.setVisibility(View.GONE);
            mTextViewBuyerInitials.setText(info.getData().getFirstName());

            if (!TextUtils.isEmpty(info.getData().getProfileImageUrl())) {
                int width = getResources().getDimensionPixelSize(R.dimen.profile_image_width);
                int height = getResources().getDimensionPixelSize(R.dimen.profile_image_height);
                MakaanNetworkClient.getInstance().getImageLoader().get(
                        ImageUtils.getImageRequestUrl(info.getData().getProfileImageUrl(), width, height, false),
                        new CustomImageLoaderListener() {

                            @Override
                            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                if(isActivityDead()){
                                    return;
                                }
                                if (b && imageContainer.getBitmap() == null) {
                                    return;
                                }
                                if(mImageViewBuyer != null) {
                                    mTextViewBuyerInitials.setVisibility(View.INVISIBLE);
                                    mImageViewBuyer.setVisibility(View.VISIBLE);
                                    mImageViewBuyer.setImageBitmap(imageContainer.getBitmap());
                                }
                            }
                        }
                );
            }
        }else{
            mTextViewBuyerInitials.setVisibility(View.GONE);
            mImageViewBuyer.setVisibility(View.VISIBLE);
            mImageViewBuyer.setImageResource(R.drawable.edit_avatar);
        }
    }

    @Override
    protected void showProgress() {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mSearchResultFrameLayout.setVisibility(View.GONE);

        Glide.with(this).load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgressBar);
    }

    @Override
    protected void showNoResults() {
        showNoResults(null);
    }

    @Override
    protected void showNoResults(String message) {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultLayout.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mSearchResultFrameLayout.setVisibility(View.GONE);

        if(message == null) {
            mNoResultsTextView.setText(R.string.generic_error);
        } else {
            mNoResultsTextView.setText(message);
        }
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mNoResultsImageView);
        Glide.with(this).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);

    }

    @Override
    protected void showNoResults(int stringId) {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultLayout.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mSearchResultFrameLayout.setVisibility(View.GONE);

        if(stringId <= 0) {
            mNoResultsTextView.setText(R.string.generic_error);
        } else {
            mNoResultsTextView.setText(stringId);
        }
        Glide.with(this).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);
    }

    @Override
    protected void showContent() {
        mContentFrameLayout.setVisibility(View.VISIBLE);
        mNoResultLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mSearchResultFrameLayout.setVisibility(View.GONE);
    }

    private void showSearchResults() {
        mContentFrameLayout.setVisibility(View.VISIBLE);
        mNoResultLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.GONE);

        mSearchResultFrameLayout.setVisibility(View.VISIBLE);
        if(mSearchLayoutFrameLayout != null && mSearchLayoutFrameLayout.getVisibility() != View.VISIBLE) {
            setShowSearchBar(true, false);
            mSearchEditTextVisible = true;
            mSearchRelativeView.setVisibility(View.VISIBLE);
            mSearchDescriptionRelativeView.setVisibility(View.GONE);
        }

        if (mSelectedSearchAdapter.getItemCount() > 0) {
            mSearchResultsFlowLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchResultsFlowLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if ((requestCode & PermissionManager.FINE_LOCATION_REQUEST)
                == PermissionManager.FINE_LOCATION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(getLocationAvailabilty()) {
                    connectLocationApiClient(MakaanLocationManager.LocationUpdateMode.ONCE);
                }
            } else if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // TODO show message or something
            }
        }
    }

    @OnClick(R.id.activity_search_base_search_no_result_action_button)
    public void onGoHomePressed(View view) {
        Intent intent = new Intent(this, HomeActivity.class);

        // as per discussion with Amit, do not clear stack
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
