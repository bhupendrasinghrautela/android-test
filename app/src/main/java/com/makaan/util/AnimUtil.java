package com.makaan.util;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;

/**
 * Animation related utilities
 * */
public class AnimUtil {

    public static void clearAnimationAfterFinish(Activity activity, Animation anim, final View view){
        if (activity==null || activity.isFinishing() || view == null || anim == null) {
            return;
        }

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (view != null) {
                    view.clearAnimation();
                }
            }
        });
    }

    /**
     * TODO This is to test circular reveal animation with activities
     * */
    public static void setUpCircularAnimation(final View rootLayout){

        if(rootLayout==null){
            return;
        }

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            return;
        }

        rootLayout.setVisibility(View.INVISIBLE);

        final ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (rootLayout.getVisibility() == View.INVISIBLE) {
                        circularRevealActivity(rootLayout);
                    }
                }
            });
        }
    }

    private static void circularRevealActivity(View rootLayout) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            return;
        }

        int cx = rootLayout.getWidth();
        int cy = rootLayout.getHeight();

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(500);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }
}