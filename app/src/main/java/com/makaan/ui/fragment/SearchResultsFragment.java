package com.makaan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.search.SearchService;
import com.makaan.ui.adapter.SearchAdapter;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchResultsFragment extends Fragment implements SearchBarFragment.SearchBarFragmentCallbacks {
    SearchAdapter mAdapter;

    @Bind(R.id.fragment_search_results_recycler_view)
    RecyclerView mRecyclerView;

    public static SearchResultsFragment init() {
        return new SearchResultsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        AppBus.getInstance().register(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }

    @Override
    public void onQueryChanged(String query) {
        if(query == null) {
            return;
        }
        SearchService service = (SearchService)MakaanServiceFactory.getInstance().getService(SearchService.class);
        try {
            service.getSearchResults(query, "gurgaon", SearchType.ALL, false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
    }
}
