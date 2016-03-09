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
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.cookie.Session;
import com.makaan.response.search.SearchResponseHelper;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_SEARCH_ITEM = 2;
    private static final int TYPE_ERROR = 3;

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
        this.mIsRecent = isRecent;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(mSearches != null && mSearches.size() > position) {
            if(SearchSuggestionType.HEADER_TEXT.getValue().equalsIgnoreCase(mSearches.get(position).type)) {
                return TYPE_HEADER;
            } else if(SearchSuggestionType.ERROR.getValue().equalsIgnoreCase(mSearches.get(position).type)) {
                return TYPE_ERROR;
            } else {
                return TYPE_SEARCH_ITEM;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER || viewType == TYPE_ERROR) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_header_item, parent, false);
            ViewHolder holder = new ViewHolder(view, viewType);
            return holder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, parent, false);
            ViewHolder holder = new ViewHolder(view, viewType);
            return holder;
        }
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
        if(mSearches != null) {
            mSearches.clear();
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int viewType;
        private final View view;
        private SearchResponseItem searchResponseItem;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if(viewType == TYPE_SEARCH_ITEM) {
                view.setOnClickListener(this);
            }
        }

        public void bindData(SearchResponseItem searchResponseItem) {
            this.searchResponseItem = searchResponseItem;

            if((SearchSuggestionType.HEADER_TEXT.getValue().equalsIgnoreCase(searchResponseItem.type) && viewType == TYPE_HEADER)
                    || (SearchSuggestionType.ERROR.getValue().equalsIgnoreCase(searchResponseItem.type) && viewType == TYPE_ERROR)) {
                ((TextView)view.findViewById(R.id.search_result_item_name_text_view)).setText(searchResponseItem.displayText.toLowerCase());
            } else {
                view.findViewById(R.id.search_result_item_progress_bar).setVisibility(View.GONE);

                // TODO need to check which kind of data we should map
                if (TextUtils.isEmpty(SearchResponseHelper.getType(searchResponseItem))) {
                    view.findViewById(R.id.search_result_item_type_text_view).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.search_result_item_type_text_view).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText(SearchResponseHelper.getType(searchResponseItem).toLowerCase());
                }

                if (SearchSuggestionType.CITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)
                        || SearchSuggestionType.LOCALITY_OVERVIEW.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                    view.findViewById(R.id.search_result_item_image_view).setVisibility(View.GONE);
                    ((TextView) view.findViewById(R.id.search_result_item_name_2_text_view)).setText(searchResponseItem.displayText.toLowerCase());
                    view.findViewById(R.id.search_result_item_name_text_view).setVisibility(View.GONE);
                    view.findViewById(R.id.search_result_item_name_2_text_view).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.search_result_item_image_view).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.search_result_item_name_text_view)).setText(searchResponseItem.displayText.toLowerCase());
                    view.findViewById(R.id.search_result_item_name_text_view).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.search_result_item_name_2_text_view).setVisibility(View.GONE);
                }

                // TODO implement lru cache
                if (SearchSuggestionType.NEARBY_PROPERTIES.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                    ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_near_by);
                    if(Session.phoneLocation == null && Session.locationRequested) {
                        view.findViewById(R.id.search_result_item_progress_bar).setVisibility(View.VISIBLE);
                    }
                } else if (mIsRecent) {
                    ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_recent);
                    if (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchResponseItem.type)
                            || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText("location".toLowerCase());
                    } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searchResponseItem.type)
                            || SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText("location".toLowerCase());
                    }
                } else {
                    if (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searchResponseItem.type)
                            || SearchSuggestionType.SUBURB.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_locality);
                        ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText("location".toLowerCase());
                    } else if (SearchSuggestionType.GOOGLE_PLACE.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_google_place);
                    } else if (SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_project);
                    } else if (SearchSuggestionType.SUGGESTION.getValue().equalsIgnoreCase(searchResponseItem.type)
                            || SearchSuggestionType.PROJECT_SUGGESTION.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_suggestion);
                    } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searchResponseItem.type)
                            || SearchSuggestionType.BUILDERCITY.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_city);
                        ((TextView) view.findViewById(R.id.search_result_item_type_text_view)).setText("location".toLowerCase());
                    } else if (SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(searchResponseItem.type)) {
                        ((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.search_builder);
                    }
                    //((ImageView) view.findViewById(R.id.search_result_item_image_view)).setImageResource(R.drawable.map_marker);
                }
                //((TextView) view.findViewById(R.id.search_result_item_address_text_view)).setText(search.displayText);
            }
        }

        @Override
        public void onClick(View v) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.SUGGESTION_NAME, searchResponseItem.displayText);
            properties.put(MakaanEventPayload.SUGGESTION_POSITION, getAdapterPosition()+1);
            properties.put(MakaanEventPayload.SUGGESTION_TYPE, searchResponseItem.type);
            properties.put(MakaanEventPayload.SUGGESTION_STRING, searchResponseItem.displayText+"_"+
                            String.valueOf(getAdapterPosition()+1)+"_"+searchResponseItem.type);
            mCallbacks.onSearchItemClick(searchResponseItem);

        }
    }

    public interface SearchAdapterCallbacks {
        void onSearchItemClick(SearchResponseItem searchResponseItem);
    }
}
