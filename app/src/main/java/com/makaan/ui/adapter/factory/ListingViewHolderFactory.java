package com.makaan.ui.adapter.factory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.adapter.constants.Constant;
import com.makaan.ui.adapter.holder.BaseListingAdapterViewHolder;
import com.makaan.ui.adapter.holder.ClusterViewHolder;
import com.makaan.ui.adapter.holder.DefaultListingViewHolder;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class ListingViewHolderFactory {
    public static BaseListingAdapterViewHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        if(viewType == Constant.LISTING_ADAPTER_ITEM_TYPE_LISTING) {
            View view = LayoutInflater.from(context).inflate(R.layout.default_listing, parent, false);
            return new DefaultListingViewHolder(view);
        } else if(viewType == Constant.LISTING_ADAPTER_ITEM_TYPE_CLUSTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.cluster_view, parent, false);
            return new ClusterViewHolder(view);
        }
        return null;
    }
}
