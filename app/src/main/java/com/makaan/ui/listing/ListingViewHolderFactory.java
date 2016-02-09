package com.makaan.ui.listing;

import android.content.Context;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.adapter.RecycleViewMode;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class ListingViewHolderFactory {
    public static BaseListingAdapterViewHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == RecycleViewMode.DATA_TYPE_LISTING.getValue()) {
            view = LayoutInflater.from(context).inflate(R.layout.default_listing, parent, false);
        } else if (viewType == RecycleViewMode.DATA_TYPE_CLUSTER.getValue()) {
            view = LayoutInflater.from(context).inflate(R.layout.cluster_view, parent, false);
        } else if (viewType == RecycleViewMode.DATA_TYPE_BUILDER.getValue()) {
            view = LayoutInflater.from(context).inflate(R.layout.serp_listing_item_builder, parent, false);
        } else if (viewType == RecycleViewMode.DATA_TYPE_SELLER.getValue()) {
            view = LayoutInflater.from(context).inflate(R.layout.serp_listing_item_seller, parent, false);
        } else if(viewType == RecycleViewMode.DATA_TYPE_COUNT.getValue()) {
            view = LayoutInflater.from(context).inflate(R.layout.serp_listing_item_count, parent, false);
            return new CountListingViewHolder(view);
        }
        return new DefaultListingViewHolder(view);
    }
}
