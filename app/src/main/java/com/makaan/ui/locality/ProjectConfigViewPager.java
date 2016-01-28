package com.makaan.ui.locality;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.makaan.event.project.SpecificationLessClickedEvent;
import com.makaan.event.project.SpecificationMoreClickedEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/28/16.
 */
public class ProjectConfigViewPager extends ViewPager{
    private int mCurrentPagePosition = 0;
    private int minHeight = 1400;
    private int initalMinHeight = 1400;
    private int lastMinHeightAdded = 0;

    public ProjectConfigViewPager(Context context) {
        super(context);
    }

    public ProjectConfigViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        if(lastMinHeightAdded !=0) {
            minHeight = minHeight - lastMinHeightAdded;
            lastMinHeightAdded = 0;
        }
        mCurrentPagePosition = position;
        minHeight = initalMinHeight;
        requestLayout();
    }

    @Subscribe
    public void onResult(SpecificationMoreClickedEvent specificationMoreClickedEvent){
        minHeight = minHeight + specificationMoreClickedEvent.heightToAdd;
        lastMinHeightAdded = specificationMoreClickedEvent.heightToAdd;
        requestLayout();
    }

    @Subscribe
    public void onResult(SpecificationLessClickedEvent specificationMoreClickedEvent){
        lastMinHeightAdded = 0;
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
