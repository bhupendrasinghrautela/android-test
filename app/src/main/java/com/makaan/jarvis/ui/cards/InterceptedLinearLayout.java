package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by sunil on 31/03/16.
 */
public class InterceptedLinearLayout extends LinearLayout {

    public interface OnInterceptTouchListener{
        void OnInterceptTouch();
    }

    private OnInterceptTouchListener mOnInterceptTouchListener;

    public InterceptedLinearLayout(Context context) {
        super(context);
    }

    public InterceptedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mOnInterceptTouchListener.OnInterceptTouch();
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener onInterceptTouchListener){
        mOnInterceptTouchListener = onInterceptTouchListener;
    }
}
