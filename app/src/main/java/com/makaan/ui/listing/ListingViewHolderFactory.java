package com.makaan.ui.listing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.constants.UIConstants;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class ListingViewHolderFactory {
    public static BaseListingAdapterViewHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        if(viewType == UIConstants.LISTING_ADAPTER_ITEM_TYPE_LISTING) {
            View view = LayoutInflater.from(context).inflate(R.layout.default_listing, parent, false);
            return new DefaultListingViewHolder(view);
        } else if(viewType == UIConstants.LISTING_ADAPTER_ITEM_TYPE_CLUSTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.cluster_view, parent, false);
            return new ClusterViewHolder(view);
        }
        return null;
    }
}
