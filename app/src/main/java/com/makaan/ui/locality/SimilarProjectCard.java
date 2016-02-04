package com.makaan.ui.locality;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class SimilarProjectCard extends CardView{

    public SimilarProjectCard(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new ClickDetector());
    }

    public SimilarProjectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new ClickDetector());
    }

    private void scaleDownAnimate() {
        ScaleAnimation scale = new ScaleAnimation((float)1.0, (float)0.8, (float)1.0, (float)0.8);
        scale.setFillAfter(true);
        scale.setDuration(300);
        this.startAnimation(scale);
    }

    private GestureDetector mGestureDetector;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    // Return false if we're scrolling in the x direction
    class ClickDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            scaleDownAnimate();
            return super.onSingleTapUp(e);
        }
    }
}
