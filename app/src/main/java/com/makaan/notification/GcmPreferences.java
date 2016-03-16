package com.makaan.notification;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sunil on 22/01/16.
 */
public class GcmPreferences {

    private static final String PREF = "makaan_buyer";
    private static final String PREF_GCM_REGID = "gcm_reg_Id";
    private static final String PREF_APP_VER = "app_version";
    private static final String PREF_IS_GCM_ID_SENT = "is_gcm_id_sent";

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }


    /**
     * Store gcm id
     *
     * @param context the Context of activity
     * @param gcmId
     * */
    public static void setGcmRegId(Context context, String gcmId) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_GCM_REGID, gcmId);
        edit.apply();
    }

    /**
     * Retrieve gcm id
     *
     * @param context
     * */
    public static String getGcmRegId(Context context) {
        return getSharedPref(context).getString(PREF_GCM_REGID, "");
    }


    /**
     * Determine if gcm id sent
     *
     * @param context the Context of activity
     * */
    public static boolean isGcmIdSent(Context context) {
        return getSharedPref(context).getBoolean(PREF_IS_GCM_ID_SENT, false);
    }


    /**
     *  Sets flag for gcm id sent
     *
     * @param context the Context of activity
     * */
    public static void setGcmIdSent(Context context) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putBoolean(PREF_IS_GCM_ID_SENT, true);
        edit.apply();
    }

    /**
     * Store app version
     *
     * @param context the Context of activity
     * @param appVersion
     * */
    public static void setAppVersion(Context context, int appVersion) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putInt(PREF_APP_VER, appVersion);
        edit.apply();
    }


    /**
     * Retrieve app version
     *
     * @param context
     * */
    public static int getAppVersion(Context context) {
        return getSharedPref(context).getInt(PREF_APP_VER, Integer.MIN_VALUE);
    }
}
