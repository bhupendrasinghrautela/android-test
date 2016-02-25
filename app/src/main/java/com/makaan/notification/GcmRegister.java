package com.makaan.notification;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunil on 05/12/15.
 */
public class GcmRegister{
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static GoogleCloudMessaging sGcm;
    private static String sRegid;
    private static Context sContext;
    private static String sUserKey;


    /**
     * Sets gcm id on GCM and makaan server
     *
     * @param context
     * @param userId
     * */
    public static void checkAndSetGcmId(Context context, String userId){
        if (checkPlayServices(context)) {
            sUserKey = userId;
            sGcm = GoogleCloudMessaging.getInstance(context);
            sRegid = getRegistrationId(context);
            if (sRegid.isEmpty()) {
                registerInBackground(context);
            }else{
            	sendRegistrationIdToBackend(sRegid);
            }
        }
    }


    private static boolean checkPlayServices(Context context) {
    	sContext = context;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                try {
                    if(context==null || ((Activity) context).isFinishing()){
                        return false;
                    }
                    GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } catch (Exception e) {
                	
                }
            } else {
                ((Activity) context).finish();
            }
            return false;
        }
        return true;
    }

    private static String getRegistrationId(Context context) {
        String registrationId = GcmPreferences.getGcmRegId(context);
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = GcmPreferences.getAppVersion(context);
        int currentVersion = CommonUtil.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private static void registerInBackground(final Context context) {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (sGcm == null) {
                        sGcm = GoogleCloudMessaging.getInstance(context);
                    }
                    sRegid = sGcm.register(GcmConstants.SENDER_ID);
                    msg = "Device registered, registration ID = " + sRegid;
                    GcmPreferences.setGcmRegId(context, sRegid);
                    GcmPreferences.setAppVersion(context, CommonUtil.getAppVersion(context));
                    sendRegistrationIdToBackend(sRegid);

                } catch (Exception ex) {
                    msg = "Error while registering for GCM:" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }

        };
        task.execute(null, null, null);
    }

    private static void sendRegistrationIdToBackend(String regId) {
        JarvisConstants.DELIVERY_ID = regId;
    }

}
