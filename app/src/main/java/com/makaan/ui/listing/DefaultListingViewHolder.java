package com.makaan.ui.listing;

import android.view.View;

import com.makaan.ui.listing.BaseListingAdapterViewHolder;
import com.makaan.ui.view.DefaultListingView;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class DefaultListingViewHolder extends BaseListingAdapterViewHolder {
    DefaultListingView view;
    public DefaultListingViewHolder(View view) {
        super(view);
        this.view = (DefaultListingView)view;
    }

    @Override
    public void populateData(Object data) {
        if(view != null) {
            view.populateData(data);
        }
    }
}
