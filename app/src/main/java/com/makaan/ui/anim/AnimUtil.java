package com.makaan.ui.anim;

import android.app.Activity;
import android.view.View;
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
}