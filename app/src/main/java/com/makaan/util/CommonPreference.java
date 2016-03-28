package com.makaan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.makaan.pojo.VersionUpdate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aishwarya on 28/03/16.
 */
public class CommonPreference {

    private static final String PREF = "makaan_buyer_user";
    private static final String PREF_MANDATORY_VERSION = "pref_mandatory_version";


    public static void saveMandatoryVersion(Context context, String version) {
        Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_MANDATORY_VERSION, version);
        edit.apply();
    }

    public static String getMandatoryVersion(Context context){
        VersionUpdate versionUpdate = new VersionUpdate();
        versionUpdate.setCurrentVersionCode(1);
        versionUpdate.setMandatoryVersionCode(1);
        try {
            JSONObject jsonObject = JsonBuilder.toJson(versionUpdate);
            return getSharedPref(context).getString(PREF_MANDATORY_VERSION,jsonObject.toString());
        } catch (JSONException e) {
            return getSharedPref(context).getString(PREF_MANDATORY_VERSION,null);
        }

    }

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
