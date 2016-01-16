package com.makaan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vaibhav on 23/12/15.
 */
public class AppUtils {

    public static SimpleDateFormat ddMMYYFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static String stripHtml(String input) {
        return android.text.Html.fromHtml(input).toString();
    }

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


    public static Date getDateFromEpoch(String epochTime) {

        return new Date(Long.parseLong(epochTime));
    }

    public static String getMMMYYYYDateStringFromEpoch(String epochTime) {
        SimpleDateFormat mmYYFormatter = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        return mmYYFormatter.format(getDateFromEpoch(epochTime));
    }


    public static String getDateStringFromEpoch(String epochTime) {
        if (epochTime != null && !TextUtils.isEmpty(epochTime)) {
            return ddMMYYFormatter.format(getDateFromEpoch(epochTime));
        }
        return "";
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
