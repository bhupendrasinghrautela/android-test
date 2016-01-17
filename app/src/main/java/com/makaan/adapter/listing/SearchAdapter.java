package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.search.Search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Search> mSearches;
    private final Context mContext;
    private final SearchAdapterCallbacks mCallbacks;

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
    }

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks, List<Search> searches) {
        mContext = context;
        mCallbacks = callbacks;
        setData((ArrayList<Search>) searches);
    }

    public void setData(ArrayList<Search> searches) {
        if (this.mSearches == null) {
            this.mSearches = new ArrayList<Search>();
        }
        this.mSearches.clear();
        this.mSearches.addAll(searches);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(mSearches.get(position));
    }

    @Override
    public int getItemCount() {
        if (mSearches == null) {
            return 0;
        }
        return mSearches.size();
    }

    public void clear() {
        mSearches.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final View view;
        private Search search;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(this);
        }

        public void bindData(Search search) {
            this.search = search;

            // TODO need to check which kind of data we should map
            ((TextView) view.findViewById(R.id.search_result_item_name_text_view)).setText(search.getDisplayText());
            if(search.getLocality() == null || TextUtils.isEmpty(search.getLocality())) {
                view.findViewById(R.id.search_result_item_address_text_view).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.search_result_item_address_text_view).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.search_result_item_address_text_view)).setText(search.getLocality());
            }
            ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText(search.getType());
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onSearchItemClick(search);
        }
    }

    public interface SearchAdapterCallbacks {
        void onSearchItemClick(Search search);
    }
}
