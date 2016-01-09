package com.makaan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.listing.Listing;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    List<String> mSearches;
    private final Context mContext;
    private final SearchAdapterCallbacks mCallbacks;

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
    }

    public SearchAdapter(Context context, SearchAdapterCallbacks callbacks, List<String> listings) {
        mContext = context;
        mCallbacks = callbacks;
        setData((ArrayList<String>) listings);
    }

    private void setData(ArrayList<String> listings) {
        if(this.mSearches == null) {
            this.mSearches = new ArrayList<String>();
        }
        this.mSearches.clear();
        this.mSearches.addAll(listings);
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
        ((ViewHolder)holder).setPropertyName("DEF");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
        public void setPropertyName(String name) {
            ((TextView)view.findViewById(R.id.search_result_item_property_name_text_view)).setText(name);
        }
    }

    public interface SearchAdapterCallbacks {
    }
}
