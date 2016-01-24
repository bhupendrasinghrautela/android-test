package com.makaan.ui.listing;

import android.view.View;

import com.makaan.activity.listing.SerpRequestCallback;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class DefaultListingViewHolder extends BaseListingAdapterViewHolder {
    AbstractListingView view;
    public DefaultListingViewHolder(View view) {
        super(view);
        this.view = (AbstractListingView)view;
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        if(view != null) {
            view.populateData(data, callback);
        }
    }
}
