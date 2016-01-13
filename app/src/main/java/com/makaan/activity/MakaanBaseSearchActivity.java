package com.makaan.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.adapter.listing.SearchAdapter;
import com.makaan.response.search.Search;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.listing.ListingService;
import com.makaan.service.search.SearchService;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/10/16.
 */
public abstract class MakaanBaseSearchActivity extends MakaanFragmentActivity implements SearchView.OnQueryTextListener,
        SearchAdapter.SearchAdapterCallbacks {

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


    @Bind(R.id.activity_search_base_layout_search_bar_search_view)
    SearchView mSearchView;

    @Bind(R.id.fragment_search_results_recycler_view)
    RecyclerView mSearchRecyclerView;


    FrameLayout mContentFrameLayout;
    private SearchAdapter mSearchAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Search> mSearches;

    @Override
    protected abstract int getContentViewId();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_search_base);
        mContentFrameLayout = (FrameLayout) findViewById(R.id.activity_search_base_content_frame_layout);

        View view = LayoutInflater.from(this).inflate(layoutResID, mContentFrameLayout, false);
        mContentFrameLayout.addView(view);
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
     * @param showSearchBar boolean to control whether search bar should show or not
     */
    protected void initUi(boolean showSearchBar) {
        setShowSearchBar(showSearchBar);

        // TODO need to check about text style for the search view
        int id = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        mSearchView.setOnQueryTextListener(this);

        // initialize adapter and manager for the recycker view
        initializeRecyclerViewData();
    }

    protected void setShowSearchBar(boolean showSearchBar) {
        if(showSearchBar) {
            mSearchLayoutFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchLayoutFrameLayout.setVisibility(View.GONE);
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if(shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    protected void initFragments(int[] fragmentHolderId, Fragment[] fragment, boolean shouldAddToBackStack) {
        if(fragmentHolderId.length != fragment.length) {
            return;
        }
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for(int i = 0; i < fragmentHolderId.length; i++) {
            fragmentTransaction.replace(fragmentHolderId[i], fragment[i], fragment.getClass().getName());
        }
        // if need to be added to the backstack, then do so
        if(shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    @Override
    public void onSearchItemClick(Search search) {
        if(mSearchLayoutFrameLayout != null) {
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }

        MakaanBuyerApplication.serpSelector.term("localityId", String.valueOf(search.getLocalityId()), true);
        new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
        mSearchAdapter.clear();

        mSearchView.setOnQueryTextListener(null);
        mSearchView.setQuery(search.getDisplayText(), false);
        mSearchView.setOnQueryTextListener(this);

        setSearchViewVisibility(false, search.getDisplayText());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText != null && !TextUtils.isEmpty(newText)) {
            mSearchResultFrameLayout.setVisibility(View.VISIBLE);
            SearchService service = (SearchService) MakaanServiceFactory.getInstance().getService(SearchService.class);
            try {
                service.getSearchResults(newText, "gurgaon", SearchType.ALL, false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            mSearchResultFrameLayout.setVisibility(View.GONE);
        }
        return true;
    }

    @OnClick(R.id.activity_search_base_layout_search_bar_back_button)
    public void onBackPressed(View view) {
        onBackPressed();
    }

    @OnClick(R.id.activity_search_base_layout_search_bar_search_image_button)
    public void onSearchPressed(View view) {
        setSearchViewVisibility(true, null);
    }

    private void setSearchViewVisibility(boolean searchViewVisible, String searchPropertiesText) {
        mSearchImageButton.setVisibility(searchViewVisible ? View.GONE : View.VISIBLE);
        mSearchPropertiesTextView.setVisibility(searchViewVisible ? View.GONE : View.VISIBLE);
        mSearchView.setVisibility(searchViewVisible ? View.VISIBLE : View.GONE);

        if(!searchViewVisible && searchPropertiesText != null && !TextUtils.isEmpty(searchPropertiesText)) {
            mSearchPropertiesTextView.setText(searchPropertiesText);
        }
    }

    public void onResults(SearchResultEvent searchResultEvent) {
        this.mSearches = searchResultEvent.searchResponse.getData();
        mSearchAdapter.setData(mSearches);
    }
}
