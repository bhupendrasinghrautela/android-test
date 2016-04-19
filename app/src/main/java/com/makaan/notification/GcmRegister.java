package com.makaan.notification;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.constants.ApiConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.jarvis.JarvisClient;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.user.UserResponse;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sunil on 05/12/15.
 */
public class GcmRegister{
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String APP_IDENTIFIER = "MakaanBuyer";
	private static GoogleCloudMessaging sGcm;
    private static String sRegid;
    private static Context sContext;
    private static String sUserKey;
    private static final String[] TOPICS = {"global"};
    public static final String SENDER_ID = "202123122479";
    public static final String SERVER_KEY = "AIzaSyB-4LwT5q2xljIhzUJ81d13wHJhpQuGFBE";



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
                JarvisConstants.DELIVERY_ID = sRegid;

                //not required, but just to make sure gcm is registered on makaan server
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
                    if(context==null || ((Activity) context).isFinishing()
                            || context instanceof MakaanBaseSearchActivity){
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
                    InstanceID instanceID = InstanceID.getInstance(context);

                    sRegid = instanceID.getToken(SENDER_ID,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    JarvisConstants.DELIVERY_ID = sRegid;

                    /*if (sGcm == null) {
                        sGcm = GoogleCloudMessaging.getInstance(context);
                    }
                    sRegid = sGcm.register(SENDER_ID);*/

                    JarvisClient.getInstance().openSocketAndFetchHistory();
                    msg = "Device registered, registration ID = " + sRegid;
                    GcmPreferences.setGcmRegId(context, sRegid);
                    GcmPreferences.setAppVersion(context, CommonUtil.getAppVersion(context));
                    sendRegistrationIdToBackend(sRegid);
                    subscribeTopics(context, sRegid);

                } catch (Exception ex) {
                    Crashlytics.log("Error while registering for GCM:" + ex.getMessage());
                    Crashlytics.logException(ex);
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
        try{
            JSONObject jObject = JsonBuilder.toJson(getGcmRegistrationDto(regId));
            String requestUrl = ApiConstants.GCM;
            MakaanNetworkClient.getInstance().post(requestUrl, jObject, null, "Android");
        }catch(IllegalArgumentException e){} catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

    }

    private static void subscribeTopics(Context context, String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }



    private static GcmRegistrationDto getGcmRegistrationDto(String regId){

        GcmRegistrationDto regDto = new GcmRegistrationDto();

        UserResponse userInfo = CookiePreferences.getLastUserInfo(sContext);
        if(userInfo !=null && userInfo.getData()!=null
                && !TextUtils.isEmpty(userInfo.getData().getEmail())){
            regDto.setEmail(userInfo.getData().getEmail());
        }else if(AccountManager.get(sContext).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE).length>0){
            String email = AccountManager.get(sContext).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)[0].name;
            regDto.setEmail(email);
        }

        if(!TextUtils.isEmpty(sUserKey)){
            regDto.setUserKey(sUserKey);
        }

        regDto.setGcmRegId(regId);
        regDto.setAppIdentifier(APP_IDENTIFIER);
        return regDto;
    }

}
