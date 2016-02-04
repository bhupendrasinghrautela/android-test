package com.makaan.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by proptiger on 28/1/16.
 */
public class LoginPreferences {
    private static final String PREF = "makaan_buyer";
    private static final String PREF_USER_INFO = "user_info";
    private static final String PREF_IS_USER_LOGGED_IN = "isLoggedIn";
    private static final String PREF_REGISTER="pref_register";


    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static void setUserInfo(Context context, String userInfo) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_USER_INFO, userInfo);
        edit.commit();
    }

    public static void setUserLoggedIn(Context context) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putBoolean(PREF_IS_USER_LOGGED_IN, true);
        edit.commit();
    }

    /* A static method for getting register flag
    *
            * @param context the Context of activity
    * @return String flag value
    * */
    public static String getRegisterFlag(Context context) {
        return getSharedPref(context).getString(PREF_REGISTER, "0");
    }

    /**
     * A static method for saving register flag
     *
     * @param context the Context of activity
     * @param flag flag value
     * */
    public static void setRegisterFlag(Context context, String flag) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_REGISTER, flag);
        edit.commit();
    }
}
