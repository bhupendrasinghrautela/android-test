package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.search.SearchResponseHelper;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<SearchResponseItem> mSearches;
    private final Context mContext;
    private final SearchAdapterCallbacks mCallbacks;
    private boolean mIsRecent;

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
    }

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks, List<SearchResponseItem> searches) {
        mContext = context;
        mCallbacks = callbacks;
        setData((ArrayList<SearchResponseItem>) searches, false);
    }

    public void setData(ArrayList<SearchResponseItem> searches, boolean isRecent) {
        if (this.mSearches == null) {
            this.mSearches = new ArrayList<SearchResponseItem>();
        }
        this.mSearches.clear();
        this.mSearches.addAll(searches);

        /*if(mSearches.size() > 0) {
            SearchResponseItem search = mSearches.get(0);
            Map<String,ApiLabel> searchResultType = MasterDataCache.getInstance().getSearchTypeMap();
            String searchField = searchResultType.get(search.type).key;

            if(SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(search.type)) {
                try {
                    SearchResponseItem item = search.clone();
                    item.displayText = "Know More \n overview - " + item.displayText;
                    item.type = SearchSuggestionType.LOCALITY_OVERVIEW.getValue();
                    if(mSearches.size() > 5) {
                        mSearches.add(5, item);
                    } else {
                        mSearches.add(item);
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            } else if(SearchSuggestionType.CITY.getValue().equalsIgnoreCase(search.type)) {
                try {
                    SearchResponseItem item = search.clone();
                    item.displayText = "Know More \n overview - " + item.displayText;
                    item.type = SearchSuggestionType.CITY_OVERVIEW.getValue();
                    if(mSearches.size() > 5) {
                        mSearches.add(5, item);
                    } else {
                        mSearches.add(item);
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }*/
        this.mIsRecent = isRecent;
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
        private SearchResponseItem searchResponseItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(this);
        }

        public void bindData(SearchResponseItem searchResponseItem) {
            this.searchResponseItem = searchResponseItem;

            // TODO need to check which kind of data we should map
            ((TextView) view.findViewById(R.id.search_result_item_name_text_view)).setText(searchResponseItem.displayText);
            if (TextUtils.isEmpty(SearchResponseHelper.getType(searchResponseItem))) {
                view.findViewById(R.id.search_result_item_type_text_view).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.search_result_item_type_text_view).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText(SearchResponseHelper.getType(searchResponseItem));
            }

            if (mIsRecent) {
                ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_history);
            } else {
                ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.map_marker);
            }
            //((TextView) view.findViewById(R.id.search_result_item_address_text_view)).setText(search.displayText);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onSearchItemClick(searchResponseItem);
        }
    }

    public interface SearchAdapterCallbacks {
        void onSearchItemClick(SearchResponseItem searchResponseItem);
    }
}
