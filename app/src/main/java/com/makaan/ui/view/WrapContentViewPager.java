package com.makaan.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.makaan.event.project.SpecificationLessClickedEvent;
import com.makaan.event.project.SpecificationMoreClickedEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/27/16.
 */

public class WrapContentViewPager extends ViewPager {

    private int mCurrentPagePosition = 0;
    private int minHeight = 1400;
    private int initalMinHeight = 1400;

    public WrapContentViewPager(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    public WrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new YScrollDetector());
    }

    private GestureDetector mGestureDetector;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the y direction
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) < Math.abs(distanceX);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            View child = getChildAt(mCurrentPagePosition);
            if (child != null) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if(h<minHeight)
                    h = minHeight;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            }else{
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.EXACTLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reMeasureCurrentPage(int position) {
        mCurrentPagePosition = position;
        minHeight = initalMinHeight;
        requestLayout();
    }

    @Subscribe
    public void onResult(SpecificationMoreClickedEvent specificationMoreClickedEvent){
        minHeight = minHeight + specificationMoreClickedEvent.heightToAdd;
        requestLayout();
    }

    @Subscribe
    public void onResult(SpecificationLessClickedEvent specificationMoreClickedEvent){
        minHeight = minHeight - specificationMoreClickedEvent.heightToRemove;
        requestLayout();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppBus.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AppBus.getInstance().unregister(this);
    }
}
