package com.makaan.ui.locality;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class SimilarProjectCard extends CardView{

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
            case (MotionEvent.ACTION_DOWN):
               scaleDownAnimate();
                return super.onTouchEvent(event);
            case (MotionEvent.ACTION_UP) :
                scaleUpAnimate();
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    public SimilarProjectCard(Context context) {
        super(context);
    }

    public SimilarProjectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void scaleDownAnimate() {
        ScaleAnimation scale = new ScaleAnimation((float)1.0, (float)0.8, (float)1.0, (float)0.8);
        scale.setFillAfter(true);
        scale.setDuration(300);
        this.startAnimation(scale);
    }

    private void scaleUpAnimate() {
        ScaleAnimation scale = new ScaleAnimation((float)1.0, (float)1.0, (float)1.0, (float)1.0);
        scale.setFillAfter(true);
        scale.setDuration(300);
        this.startAnimation(scale);
    }
}
