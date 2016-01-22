package com.makaan.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class ParallexScrollview extends ScrollView {

    public interface OnScrollChangedListener {
        public void onScrollChanged(int deltaX, int deltaY);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ParallexScrollview(Context context) {
        super(context);
    }

    public ParallexScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallexScrollview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(l - oldl, t - oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }
}