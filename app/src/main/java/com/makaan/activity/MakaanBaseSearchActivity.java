package com.makaan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.SearchAdapter;
import com.makaan.response.search.SearchResponseHelper;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SearchService;

import com.makaan.util.RecentSearchManager;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/10/16.
 */
public abstract class MakaanBaseSearchActivity extends MakaanFragmentActivity implements
        SearchAdapter.SearchAdapterCallbacks, TextWatcher {

    @Bind(R.id.activity_search_base_search_frame_layout)
    FrameLayout mSearchLayoutFrameLayout;
    @Bind(R.id.activity_search_base_search_results_frame_layout)
    FrameLayout mSearchResultFrameLayout;

    @Bind(R.id.activity_search_base_layout_search_bar_builder_image_view)
    ImageView mBuilderImageView;
    @Bind(R.id.activity_search_base_layout_search_bar_search_image_button)
    ImageButton mSearchImageButton;

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


    FrameLayout mContentFrameLayout;
    private SearchAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<SearchResponseItem> mSearches;
    protected FrameLayout mMainFrameLayout;

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
    }

    private void initializeRecyclerViewData() {

        mSearchAdapter = new SearchAdapter(this, this);
        mLayoutManager = new LinearLayoutManager(this);

        mSearchRecyclerView.setLayoutManager(mLayoutManager);
        mSearchRecyclerView.setAdapter(mSearchAdapter);
    }

    /**
     * must call this function from onCreate function by child activity
     * with default preference of whether search bar should show or not
     *
     * @param showSearchBar boolean to control whether search bar should show or not
     */
    protected void initUi(boolean showSearchBar) {
        setShowSearchBar(showSearchBar);

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

        // initialize adapter and manager for the recycler view
        initializeRecyclerViewData();
    }

    protected void setShowSearchBar(boolean showSearchBar) {
        if (showSearchBar) {
            mSearchLayoutFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchLayoutFrameLayout.setVisibility(View.GONE);
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchItemClick(SearchResponseItem searchResponseItem) {
        if (mSearchLayoutFrameLayout != null) {
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }

        // TODO need to handle all cases
        SearchResponseHelper.resolveSearch(searchResponseItem, this);

        mSearchAdapter.clear();

        mSearchEditText.removeTextChangedListener(this);
        mSearchEditText.setText(searchResponseItem.displayText);
        mSearchEditText.addTextChangedListener(this);

        setSearchViewVisibility(false, searchResponseItem.displayText);

        RecentSearchManager.getInstance(this).addEntryToRecentSearch(searchResponseItem, this);

        showKeypad(mSearchEditText, false);
    }

    private void showKeypad(View view, boolean show) {
        if(show) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 400);
        } else {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }, 400);
        }
    }

    @OnClick(R.id.activity_search_base_layout_search_bar_back_button)
    public void onBackPressed(View view) {

        // change search view visibility to gone if it is visible
        if(mSearchRelativeView.getVisibility() == View.VISIBLE) {
            setSearchViewVisibility(false, null);
            // hide keypad
            showKeypad(mSearchEditText, false);
        } else {
            onBackPressed();
        }
    }

    @OnClick({R.id.activity_search_base_layout_search_bar_search_image_button, R.id.activity_search_base_layout_search_bar_search_text_view})
    public void onSearchPressed(View view) {
        setSearchViewVisibility(true, null);
    }

    private void setSearchViewVisibility(boolean searchViewVisible, String searchPropertiesText) {
        mSearchDescriptionRelativeView.setVisibility(searchViewVisible ? View.GONE : View.VISIBLE);
        mSearchRelativeView.setVisibility(searchViewVisible ? View.VISIBLE : View.GONE);
        mSearchEditText.requestFocus();

        if (!searchViewVisible && searchPropertiesText != null && !TextUtils.isEmpty(searchPropertiesText)) {
            mSearchPropertiesTextView.setText(searchPropertiesText);
        }
        if(searchViewVisible) {
            showEmptySearchResults();
        }

        if(!searchViewVisible) {
            if (mSearchLayoutFrameLayout != null) {
                mSearchResultFrameLayout.setVisibility(View.GONE);
            }
        }

        // show keypad id we need to show serch view
        if(searchViewVisible) {
            showKeypad(mSearchEditText, true);
        }
    }

    private void showEmptySearchResults() {
        ArrayList<SearchResponseItem> searches = RecentSearchManager.getInstance(this).getRecentSearches(this);
        if(searches != null && searches.size() > 0) {
            mSearchResultFrameLayout.setVisibility(View.VISIBLE);
            mSearches = searches;
            mSearchAdapter.setData(searches, true);
        } else {
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }
    }

    public void onResults(SearchResultEvent searchResultEvent) {
        mSearchResultFrameLayout.setVisibility(View.VISIBLE);
        this.mSearches = searchResultEvent.searchResponse.getData();
        mSearchAdapter.setData(mSearches, false);
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
                service.getSearchResults(editable.toString(), null, SearchType.ALL, false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
//            mSearchResultFrameLayout.setVisibility(View.GONE);
            showEmptySearchResults();
        }
    }
    @OnClick(R.id.activity_search_base_layout_search_bar_search_close_button)
    public void onDeletePressed(View view) {
        mSearchEditText.setText("");
    }
}
