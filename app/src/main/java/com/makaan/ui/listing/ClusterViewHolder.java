package com.makaan.ui.listing;

import android.view.View;

import com.makaan.ui.listing.BaseListingAdapterViewHolder;
import com.makaan.ui.view.ClusterListingView;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class ClusterViewHolder extends BaseListingAdapterViewHolder {
    ClusterListingView view;
    public ClusterViewHolder(View view) {
        super(view);
        this.view = (ClusterListingView)view;
    }

    @Override
    public void populateData(Object data) {
        if(view != null) {
            view.populateData(data);
        }
    }
}
