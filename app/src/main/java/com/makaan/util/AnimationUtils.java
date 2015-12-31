package com.makaan.util;

import android.app.Activity;
import android.content.Context;

/**
 * Created by vaibhav on 23/12/15.
 */
public class AnimationUtils {

    public static void backActivityAnimation(Context context) {

        try {
            ((Activity) context).overridePendingTransition(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        } catch (Exception e) {

        }
    }



}
