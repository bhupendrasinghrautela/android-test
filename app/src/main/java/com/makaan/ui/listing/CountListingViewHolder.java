package com.makaan.ui.listing;

import android.view.View;

import com.makaan.activity.listing.SerpRequestCallback;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class CountListingViewHolder extends BaseListingAdapterViewHolder {
    View view;
    public CountListingViewHolder(View view) {
        super(view);
        this.view = view;
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback, int position) {
        if(view != null) {
            if(view instanceof CountListingView) {
                ((CountListingView) view).populateData(data, callback);
            }
        }
    }
}
