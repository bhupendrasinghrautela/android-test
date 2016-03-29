package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.makaan.activity.listing.SerpRequestCallback;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/7/16.
 */
public abstract class AbstractListingView extends FrameLayout {
    Context mContext;
    public AbstractListingView(Context context) {
        super(context);
        mContext = context;
    }

    public AbstractListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AbstractListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void populateData(Object data, SerpRequestCallback callback, int position) {
        ButterKnife.bind(this, this);
    }
}
