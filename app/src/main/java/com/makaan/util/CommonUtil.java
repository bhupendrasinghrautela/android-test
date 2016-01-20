package com.makaan.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.makaan.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aishwarya on 06/01/16.
 */
public class CommonUtil {

    private static final String TAG = "MAKAAN-BUYER";
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static Iterator sortedIterator(Iterator it, Comparator comparator) {

        List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }

        Collections.sort(list, comparator);
        return list.iterator();
    }

    public static final void TLog(String msg){
        if(DEBUG){
            Log.e(TAG, msg);
        }
    }

    public static final void TLog(String msg, Exception e){
        if(DEBUG){
            Log.e(TAG, msg, e);
        }
    }

    public static int pixelToDp(Context context, int px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static int dpToPixel(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
