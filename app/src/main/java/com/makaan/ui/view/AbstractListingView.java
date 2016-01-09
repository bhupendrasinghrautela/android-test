package com.makaan.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/7/16.
 */
public abstract class AbstractListingView extends CardView {
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

    public void populateData(Object data) {
        ButterKnife.bind(this, this);
    }
}
