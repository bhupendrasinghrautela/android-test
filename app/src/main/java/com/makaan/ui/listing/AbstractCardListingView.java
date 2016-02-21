package com.makaan.ui.listing;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.makaan.activity.listing.SerpRequestCallback;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/7/16.
 */
public abstract class AbstractCardListingView extends CardView {
    Context mContext;
    public AbstractCardListingView(Context context) {
        super(context);
        mContext = context;
    }

    public AbstractCardListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AbstractCardListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void populateData(Object data, SerpRequestCallback callback) {
        ButterKnife.bind(this, this);
    }
}
