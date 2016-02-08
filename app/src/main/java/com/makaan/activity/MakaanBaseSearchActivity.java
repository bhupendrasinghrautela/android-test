package com.makaan.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerJourneyActivity;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.adapter.listing.SearchAdapter;
import com.makaan.adapter.listing.SelectedSearchAdapter;
import com.makaan.cookie.Session;
import com.makaan.response.search.SearchResponseHelper;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.LocationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SearchService;

import com.makaan.ui.listing.CustomFlowLayout;
import com.makaan.util.RecentSearchManager;
import com.makaan.util.StringUtil;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/10/16.
 */
public abstract class MakaanBaseSearchActivity extends MakaanFragmentActivity implements
        SearchAdapter.SearchAdapterCallbacks, TextWatcher, CustomFlowLayout.ItemRemoveListener {

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

    @Bind(R.id.activity_search_base_layout_search_bar_user_image_view)
    ImageView mUserImageView;

    @Bind(R.id.activity_search_base_layout_search_bar_search_image_view)
    ImageView mSearchImageView;

    @Bind(R.id.activity_search_base_layout_search_bar_search_image_button)
    ImageButton mSearchImageButton;
    @Bind(R.id.activity_search_base_layout_search_bar_search_close_button)
    ImageButton mDeleteButton;

    @Bind(R.id.activity_search_base_layout_search_bar_search_text_view)
    TextView mSearchPropertiesTextView;

    @Bind(R.id.fragment_search_results_recycler_view)
    RecyclerView mSearchRecyclerView;

    @Bind(R.id.activity_search_base_layout_search_bar_description_relative_view)
    RelativeLayout mSearchDescriptionRelativeView;
    @Bind(R.id.activity_search_base_layout_search_bar_search_relative_view)
    RelativeLayout mSearchRelativeView;

    @Bind(R.id.activity_search_base_layout_search_bar_search_edit_text)
    EditText mSearchEditText;

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
        mSearchImageViewX = size.x - (this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_back_button_width)
                + this.getResources().getDimensionPixelSize(R.dimen.activity_search_base_layout_search_bar_back_button_margin_left)
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

        // TODO need to check about text style for the search view
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
                setSearchResultFrameLayoutVisibility(false);
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

    @Override
    public void onSearchItemClick(SearchResponseItem searchResponseItem) {
        setSearchResultFrameLayoutVisibility(false);

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
            RecentSearchManager.getInstance(this).addEntryToRecentSearch(searchResponseItem, this);
        }

        /*if(!areListingsAvailable() && (SearchSuggestionType.CITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)
                || SearchSuggestionType.LOCALITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)
                || SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchResponseItem.type))) {
            finish();
        }*/

        if(supportsListing() && !SearchSuggestionType.NEARBY_PROPERTIES.getValue().equals(searchResponseItem.type)) {
            setTitle(searchResponseItem.displayText);
        }
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

        // hide the keypad
        showKeypad(mSearchEditText, false);

        // TODO need to handle all cases
        SearchResponseHelper.resolveSearch(mSelectedSearches, this, supportsListing());

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
    public void onBackPressed(View view) {
        onBackPressed();
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

    protected boolean areListingsAvailable() {
        return false;
    }

    @OnClick({R.id.activity_search_base_layout_search_bar_search_image_button, R.id.activity_search_base_layout_search_bar_search_text_view})
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
                setSearchResultFrameLayoutVisibility(false);
            }
        }

        // show keypad id we need to show search view
        if(searchViewVisible) {
            showKeypad(mSearchEditText, true);
        }

        // make sure search bar is not  collapsing on scrolling when search results are available
        setSearchBarCollapsible(searchViewVisible ? false : needScrollableSearchBar());

        // show animation for the glass icon according to visibility sent
        if(searchViewVisible) {
            mSearchDescriptionRelativeView.setVisibility(View.GONE);
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
                                if(hintText != null) {
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
                mDeleteButton.setBackgroundResource(R.drawable.close_white);
                mSearchImageView.setVisibility(View.GONE);
            }
        } else {
            if(TextUtils.isEmpty(mSearchEditText.getText().toString())) {
                mSearchImageView.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.GONE);
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
                                if(mSearchDescriptionRelativeView != null) {
                                    mSearchDescriptionRelativeView.setVisibility(View.VISIBLE);
                                }
                                if(mSearchRelativeView != null) {
                                    mSearchRelativeView.setVisibility(View.GONE);
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
                mSearchRelativeView.setVisibility(View.GONE);
            }
        }

        setIsJarvisVisibile(!searchViewVisible);
    }

    private void showEmptySearchResults() {
        if(mSelectedSearches.size() > 0) {
            LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
            service.getTopNearbyLocalitiesAsSearchResult(mSelectedSearches.get(mSelectedSearches.size() - 1));
            setSearchResultFrameLayoutVisibility(true);
        } else {
            ArrayList<SearchResponseItem> searches = RecentSearchManager.getInstance(this).getRecentSearches(this);
            if (searches != null && searches.size() > 0) {
                mSearchResultReceived = false;
                setSearchResultFrameLayoutVisibility(true);
                mSearches = searches;
                clearSelectedSearches();
                addNearbyPropertiesSearchItem();
                mSearchAdapter.setData(mAvailableSearches, true);
            } else {
                if(this instanceof HomeActivity) {
                    // check if we have user's location
                    if(Session.myLocation != null) {
                        LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
                        service.getTopLocalitiesAsSearchResult(Session.myLocation);
                    }
                    setSearchResultFrameLayoutVisibility(true);
                } else {
                    // we need empty layout even when no search results are present
                    setSearchResultFrameLayoutVisibility(true);
                }
            }
        }
    }

    private void addNearbyPropertiesSearchItem() {
        if(Session.myLocation != null) {
            SearchResponseItem item = new SearchResponseItem();
            item.type = SearchSuggestionType.NEARBY_PROPERTIES.getValue();
            item.displayText = "properties near my location";
            item.latitude = Session.myLocation.centerLatitude;
            item.longitude = Session.myLocation.centerLongitude;
            mAvailableSearches.add(0, item);
        }
    }

    private void setSearchResultFrameLayoutVisibility(boolean visible) {
        mSearchResultFrameLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(visible) {
            if (mSelectedSearchAdapter.getItemCount() > 0) {
                mSearchResultsFlowLayout.setVisibility(View.VISIBLE);
            } else {
                mSearchResultsFlowLayout.setVisibility(View.GONE);
            }
        }
    }

    public void onResults(SearchResultEvent searchResultEvent) {
        Log.d("DEBUG", searchResultEvent.searchResponse.toString());
        if(null!=searchResultEvent.error) {
            //TODO handle error
            return;
        }
        if(mSearchResultFrameLayout.getVisibility() == View.VISIBLE) {
            mSearchResultReceived = true;
            setSearchResultFrameLayoutVisibility(true);
            this.mSearches = searchResultEvent.searchResponse.getData();
            clearSelectedSearches();
            if(TextUtils.isEmpty(mSearchEditText.getText())) {
                addNearbyPropertiesSearchItem();
            }
            mSearchAdapter.setData(mAvailableSearches, false);
        }
    }

    private void clearSelectedSearches() {
        mAvailableSearches.clear();
        mAvailableSearches.addAll(mSearches);
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
                Log.d("DEBUG", editable.toString());
                // if selected search adapter has items already, then it means there is some locality already selected
                // so we need to search for localities only in the selected city
                if(mSelectedSearchAdapter.getItemCount() > 0) {
                    service.getSearchResults(editable.toString(), (mSerpContext == SERP_CONTEXT_BUY ? "buy" : "rent"),
                            mSelectedSearches.get(0).city, new SearchType[] {SearchType.LOCALITY, SearchType.SUBURB}, false);
                } else {
                    // search for everything everywhere
                    service.getSearchResults(editable.toString(), (mSerpContext == SERP_CONTEXT_BUY ? "buy" : "rent"),
                            null, SearchType.ALL, true);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // handle visibility of delete/cross button
            mDeleteButton.setBackgroundResource(R.drawable.close_white);
        } else {
//            mSearchResultFrameLayout.setVisibility(View.GONE);
            // show recent searches as there is no text in the search view
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
            mSelectedSearches.remove((SearchResponseItem) item);
            // enable search edit text
            mSearchEditText.setEnabled(true);

            // show search edittext hint based on current no of selected searches
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

            clearSelectedSearches();
            mSearchAdapter.setData(mAvailableSearches, !mSearchResultReceived);
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

    /**
     * open buyer journey activity
     */
    @OnClick(R.id.activity_search_base_layout_search_bar_user_image_view)
    public void click(){
        startActivity(new Intent(MakaanBaseSearchActivity.this, BuyerJourneyActivity.class));
    }

    /**
     * set title of the activity
     * @param title title to display in the toolbar*
     */
    protected void setTitle(String title) {
        mSearchPropertiesTextView.setText(title);
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
    }

    protected void applySearch(ArrayList<SearchResponseItem> searches) {
        if((mSelectedSearches == null || mSelectedSearches.size() == 0)
                && (searches != null && searches.size() > 0)) {
            for(SearchResponseItem item : searches) {
                addSearchInWrapLayout(item);
            }
        }
    }

    protected abstract boolean needScrollableSearchBar();
    protected abstract boolean supportsListing();
}
