package com.makaan.fragment.buyerJourney;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.saveSearch.SaveSearch;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;

/**
 * Created by aishwarya on 29/01/16.
 */
public class SaveSearchFragment extends MakaanBaseFragment {

    @Bind(R.id.save_search_recycler_view)
    RecyclerView mRecyclerView;

    private SaveSearchAdapter mAdapter;
    private Context context;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_save_search;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
    }

    private void initView() {
        LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    private class SaveSearchAdapter extends RecyclerView.Adapter<SaveSearchAdapter.ViewHolder> {
        private List<SaveSearch> savedSearches;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView saveSearchName;
            public TextView saveSearchFilter;
            public TextView saveSearchPlace;

            public ViewHolder(View v) {
                super(v);
                saveSearchName = (TextView) v.findViewById(R.id.search_name);
                saveSearchFilter = (TextView) v.findViewById(R.id.filter_name);
                saveSearchPlace = (TextView) v.findViewById(R.id.place_name);
            }
        }

        public SaveSearchAdapter(List<SaveSearch> savedSearches) {
            this.savedSearches = savedSearches;
        }

        @Override
        public SaveSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_search, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.saveSearchName.setText(savedSearches.get(position).name);
            holder.saveSearchFilter.setText(savedSearches.get(position).searchQuery);
        }

        @Override
        public int getItemCount() {
            return savedSearches.size();
        }

    }


    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        SaveSearchGetEvent saveSearchGetEvent1 = saveSearchGetEvent;
        mAdapter = new SaveSearchAdapter(saveSearchGetEvent.saveSearchArrayList);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

}
