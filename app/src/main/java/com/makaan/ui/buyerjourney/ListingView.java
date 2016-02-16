package com.makaan.ui.buyerjourney;

import android.content.Context;
import android.util.AttributeSet;

import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.ui.listing.AbstractListingView;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ListingView extends AbstractListingView {
    public ListingView(Context context) {
        super(context);
    }

    public ListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);
    }
}
