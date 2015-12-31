package com.makaan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vaibhav on 23/12/15.
 */
public class AppUtils {

    public static SimpleDateFormat ddMMYYFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static boolean haveNetworkConnection(Context activityContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static String getDeviceId(Context activityContext) {
        return Settings.Secure.getString(activityContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static long getCurrentTimeStamp() {
        try {
            return System.currentTimeMillis() / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static Date getDateFromEpoch(String epochTime) {

        return new Date(Long.parseLong(epochTime));
    }

    public static String getMMMYYYYDateStringFromEpoch(String epochTime) {
        SimpleDateFormat mmYYFormatter = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        return mmYYFormatter.format(getDateFromEpoch(epochTime));
    }


    public static String getDateStringFromEpoch(String epochTime) {
        return ddMMYYFormatter.format(getDateFromEpoch(epochTime));

    }

    public static Long getElapsedDaysFromNow(String epochTime) {
        Date date1 = getDateFromEpoch(epochTime);

        long diff = Math.abs(new Date().getTime() - date1.getTime());
        return diff / (24 * 60 * 60 * 100);
    }


    public static String stripContent(String text, int length, boolean wordwise) {
        if (null == text) {
            return null;
        }
        StringBuilder contentTrimmed = new StringBuilder();
        int lastspace;

        if (text.length() > length) {
            contentTrimmed.append(text.substring(0, length));
            if (wordwise) {
                lastspace = contentTrimmed.lastIndexOf(" ");
                if (lastspace != -1) {
                    contentTrimmed = contentTrimmed.append(contentTrimmed.substring(0, lastspace));
                }
            }

        } else {
            contentTrimmed.append(text);

        }
        return contentTrimmed.toString();
    }


}
