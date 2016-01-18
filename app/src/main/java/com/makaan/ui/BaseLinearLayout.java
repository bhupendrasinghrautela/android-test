package com.makaan.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;

/**
 * Created by aishwarya on 06/01/16.
 */
public abstract class BaseLinearLayout<D> extends LinearLayout {
    protected Context mContext;

    public BaseLinearLayout(Context context) {
        this(context, null);
    }

    public BaseLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public abstract void bindView(D item);
}
