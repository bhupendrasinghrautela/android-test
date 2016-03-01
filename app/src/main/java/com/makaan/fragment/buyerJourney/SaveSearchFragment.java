package com.makaan.fragment.buyerJourney;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.listing.SetAlertsDialogFragment;
import com.makaan.pojo.SelectorParser;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
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

        if(MasterDataCache.getInstance().getSavedSearch() != null) {

            mAdapter = new SaveSearchAdapter(MasterDataCache.getInstance().getSavedSearch());
            if (mRecyclerView != null) {
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private void initView() {
        LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


    private class SaveSearchAdapter extends RecyclerView.Adapter<SaveSearchAdapter.ViewHolder> {
        private List<SaveSearch> savedSearches;
        private int position;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final ImageView deleteImageView;
            // each data item is just a string in this case
            public TextView saveSearchName;
            public TextView saveSearchFilter;
            public TextView saveSearchPlace;
            public Long searchId;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                saveSearchName = (TextView) v.findViewById(R.id.search_name);
                saveSearchFilter = (TextView) v.findViewById(R.id.filter_name);
                saveSearchPlace = (TextView) v.findViewById(R.id.place_name);
                deleteImageView = (ImageView) v.findViewById(R.id.iv_cancel);
                deleteImageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(v instanceof ImageView) {
                    if(searchId != null) {
                        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).removeSavedSearch(searchId);
                    }
                } else if(savedSearches != null && savedSearches.size() > position) {
                    SerpRequest request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                    request.launchSerp(getActivity(), savedSearches.get(position).searchQuery);
                }
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
            this.position = position;
            String name = savedSearches.get(position).name;
            holder.searchId = savedSearches.get(position).id;
            if(!TextUtils.isEmpty(name)) {
                int semiIndex = name.indexOf(";");
                String finalName = name;
                if(semiIndex >= 0) {
                    finalName = name.substring(0, semiIndex);
                    holder.saveSearchName.setText(finalName);
                    if(semiIndex < name.length() - 1) {
                        String[] filterArray = name.substring(semiIndex + 1, name.length()).split(SetAlertsDialogFragment.SEPARATOR_FILTER);
                        StringBuilder builder = new StringBuilder();
                        String separator = "";
                        if(filterArray != null) {
                            for (String filter : filterArray) {
                                builder.append(separator);
                                builder.append(filter);
                                separator = "\n";
                            }
                            holder.saveSearchFilter.setText(builder.toString());
                        }
                    }
                } else {
                    holder.saveSearchName.setText(finalName);
                }
            }
        }

        @Override
        public int getItemCount() {
            return savedSearches.size();
        }

    }


    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        if(null== saveSearchGetEvent || null!=saveSearchGetEvent.error){
            //TODO handle error
            return;
        }
        SaveSearchGetEvent saveSearchGetEvent1 = saveSearchGetEvent;
        mAdapter = new SaveSearchAdapter(saveSearchGetEvent.saveSearchArrayList);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

}
