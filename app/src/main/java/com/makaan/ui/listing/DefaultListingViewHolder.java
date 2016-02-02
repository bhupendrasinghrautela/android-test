package com.makaan.ui.listing;

import android.view.View;
import android.widget.TextView;

import com.makaan.activity.listing.SerpRequestCallback;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class DefaultListingViewHolder extends BaseListingAdapterViewHolder {
    View view;
    public DefaultListingViewHolder(View view) {
        super(view);
        this.view = view;
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        if(view != null) {
            if(view instanceof TextView) {
                ((TextView)view).setText(data.toString());
            } else {
                if(view instanceof AbstractListingView) {
                    ((AbstractListingView)view).populateData(data, callback);
                }
            }
        }
    }
}
