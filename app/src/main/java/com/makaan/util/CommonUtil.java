package com.makaan.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.makaan.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    /**
     * Utility for convert bundle to JSONObject
     * @param bundle input
     * @return JSONObject
     * */
    public static JSONObject bundletoJSONObject(Bundle bundle){
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                if(key.equalsIgnoreCase("extra") || key.equalsIgnoreCase("extras")){
                    try{
                        if(!TextUtils.isEmpty(bundle.get(key).toString())){
                            JSONObject extra = new JSONObject(bundle.get(key).toString());
                            Iterator<String> extraKeys = extra.keys();
                            while(extraKeys.hasNext()){
                                String extraKey = extraKeys.next();
                                json.put(extraKey, extra.get(extraKey));
                            }
                        }
                    }catch(Exception e){
                    }

                }else{
                    json.put(key, bundle.get(key));
                }
            } catch(JSONException e) {
                //Handle exception here
            }
        }
        return json;
    }

    public static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * check email validation
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
