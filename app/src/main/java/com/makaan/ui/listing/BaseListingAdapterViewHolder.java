package com.makaan.ui.listing;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.makaan.activity.listing.SerpRequestCallback;

/**
 * Created by rohitgarg on 1/6/16.
 */
public abstract class BaseListingAdapterViewHolder extends RecyclerView.ViewHolder {
    public BaseListingAdapterViewHolder(View view) {
        super(view);
    }

    abstract public void populateData(Object data, SerpRequestCallback callback);
}
