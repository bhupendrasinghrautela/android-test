package com.makaan.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.makaan.response.user.UserResponse;
import com.makaan.util.JsonParser;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sunil on 25/01/16.
 */
public class CookiePreferences {

    private static final String PREF = "makaan_buyer_user";

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private static final String PREF_LAST_USER = "last_user_json";
    private static final String PREF_USER_INFO = "user_info";
    private static final String PREF_IS_USER_LOGGED_IN = "isLoggedIn";
    private static final String PREF_COOKIE = "makaan_cookies";

    private static final String PREF_USER_NAME = "user_name";
    private static final String PREF_PASSWORD = "password";

    private static final long BUYER_JOURNEY_LAST_POPUP_TIMEOUT = 3600 * 60 * 24 * 4;
    private static final String BUYER_JOURNEY_LAST_POPUP_TIMESTAMP = "buyer_last_timestamp";
    /**
     * A static method for saving user info
     *
     * @param context the Context of activity
     * @param userInfo the user info json string
     * */
    public static void setUserInfo(Context context, String userInfo) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_USER_INFO, userInfo);
        edit.commit();

        setLastUserInfo(context, userInfo);
    }


    /**
     * A static method for getting user info
     *
     * @param context the Context of activity
     * @return String the user info json string
     * */
    public static UserResponse getUserInfo(Context context) {
        return (UserResponse) JsonParser.parseJson(
                getSharedPref(context).getString(PREF_USER_INFO, null), UserResponse.class);
    }

    /**
     * Sets flag for user logged in
     *
     * @param context the Context of activity
     * */
    public static void setUserLoggedIn(Context context) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putBoolean(PREF_IS_USER_LOGGED_IN, true);
        edit.commit();
    }

    /**
     *  Sets flag for user logged out
     *
     * @param context the Context of activity
     * */
    public static void setUserLoggedOut(Context context) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putBoolean(PREF_IS_USER_LOGGED_IN, false);
        edit.commit();
    }

    /**
     * Determine if user is logged in
     *
     * @param context the Context of activity
     * @return boolean flag telling if user is logged in
     * */
    public static boolean isUserLoggedIn(Context context) {
        return getSharedPref(context).getBoolean(PREF_IS_USER_LOGGED_IN, false);
    }

    public static void setLastUserInfo(Context context, String uInfo){
        if(uInfo==null){
            return;
        }
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_LAST_USER, uInfo);
        edit.commit();
    }

    public static UserResponse getLastUserInfo(Context context) {
        return (UserResponse) JsonParser.parseJson(
                getSharedPref(context).getString(PREF_LAST_USER, null), UserResponse.class);
    }


    /**
     * A static method for storing a cookie in preferences to make it persistent
     *
     * @param context the Context of activity
     * @param uri the URI for which cookie has to be stored
     * @param cookie the HttpCookie to be stored
     *
     * */
    public static void setCookie(Context context, URI uri, HttpCookie cookie) {
        if(TextUtils.isEmpty(cookie.getValue())){
            return;
        }

        SharedPreferences cookiePreferences = getCookieSharedPref(context);
        SharedPreferences.Editor ediWriter = cookiePreferences.edit();
        HashSet<String> setCookies = new HashSet<String>();
        setCookies.add(cookie.toString());
        HashSet<String> emptyCookieSet = new HashSet<String>();
        if (cookiePreferences.contains(uri.getHost())) {
            emptyCookieSet = new HashSet<String>(getStringSet(cookiePreferences, uri.getHost()));
            if (!emptyCookieSet.isEmpty()) {

                //CommonUtil.TLog("cookie.toString() " + cookie.toString());
                //CommonUtil.TLog("cookie.getName() " + cookie.getName());

                //Remove any existing cookie with same name
                Iterator<String> cookieSet = emptyCookieSet.iterator();
                if(cookieSet!=null){
                    while(cookieSet.hasNext()){
                        String cookieString = cookieSet.next();
                        if(!TextUtils.isEmpty(cookieString) && !TextUtils.isEmpty(cookie.getName())){
                            if(cookieString.contains(cookie.getName())){
                                try{
                                    cookieSet.remove();
                                    //CommonUtil.TLog("Removed older cookie to refresh cookie store " + cookieString);
                                }catch(Exception e){}
                            }
                        }
                    }
                }

                if (!emptyCookieSet.contains(cookie.toString())) {
                    emptyCookieSet.add(cookie.toString());
                    putStringSet(ediWriter, uri.getHost(), emptyCookieSet);
                }
            }else{
                putStringSet(ediWriter, uri.getHost(), setCookies);
            }
        } else {
            putStringSet(ediWriter, uri.getHost(), setCookies);
        }
        ediWriter.commit();
    }


    /**
     * A static method for fetching  all the cookies from the store
     *
     * @return Map of url and cookies
     * */
    public static Map<String, ?> getCookies(Context context) {
        return getCookieSharedPref(context).getAll();
    }


    public static HashSet<String> getStringSet(final SharedPreferences sharedPreferences, final String key) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            final HashSet<String> out = new HashSet<String>();
            final String base = sharedPreferences.getString(key, null);
            if (base != null) {
                out.addAll(Arrays.asList(base.split(",")));
            }
            return out;
        }else{
            return (HashSet<String>) sharedPreferences.getStringSet(key, new HashSet<String>());
        }
    }


    private static SharedPreferences getCookieSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF_COOKIE, Context.MODE_PRIVATE);
    }


    private static void putStringSet(final SharedPreferences.Editor editor, final String key,
                                     final HashSet<String> stringSet) {
        if(editor==null || stringSet==null){
            return;
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            Iterator<String> cookieSet = stringSet.iterator();
            int i = 0;
            StringBuilder cookieBuilder = new StringBuilder();
            while(cookieSet.hasNext()){
                if(i>0){
                    cookieBuilder.append(",");
                }
                cookieBuilder.append(cookieSet.next());
                i++;
            }
            editor.putString(key, cookieBuilder.toString());
        }else{
            editor.putStringSet(key, stringSet);
        }
    }

    /**
     * A static method for getting username for remember me
     *
     * @param context the Context of activity
     * @return selected Index from server
     * */
    public static String getUserName(Context context) {
        return getSharedPref(context).getString(PREF_USER_NAME, "");
    }


    /**
     * A static method for saving username of user for remember me
     *
     * @param context the Context of activity
     * @param name selected Index
     * */
    public static void setUserName(Context context, String name) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_USER_NAME, name);
        edit.commit();
    }

    /**
     * A static method for getting password of user remember me
     *
     * @param context the Context of activity
     * @return selected Index from server
     * */
    public static String getPassword(Context context) {
        return getSharedPref(context).getString(PREF_PASSWORD, "");
    }


    /**
     * A static method for saving password for user remember me
     *
     * @param context the Context of activity
     * @param password selected Index
     * */
    public static void setPassword(Context context, String password) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_PASSWORD, password);
        edit.commit();
    }


    public static void setBuyerJourneyPopupTimestamp(Context context) {
        SharedPreferences.Editor edit = getSharedPref(context).edit();
        edit.putLong(BUYER_JOURNEY_LAST_POPUP_TIMESTAMP, System.currentTimeMillis());
        edit.apply();
    }


    public static boolean shouldDisplayBuyerJourney(Context context) {
        return System.currentTimeMillis()
                - getSharedPref(context).getLong(BUYER_JOURNEY_LAST_POPUP_TIMESTAMP, 0)
                >= BUYER_JOURNEY_LAST_POPUP_TIMEOUT;
    }
}
