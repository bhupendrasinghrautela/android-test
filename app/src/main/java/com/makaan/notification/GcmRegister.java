package com.makaan.notification;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request.Method;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.makaan.constants.ApiConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.ResponseError;
import com.makaan.response.user.UserResponse;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.prefs.Preferences;

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
            	//sendRegistrationIdToBackend(sRegid);
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
                    //sendRegistrationIdToBackend(sRegid);

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
        try{
            JSONObject jObject = JsonBuilder.toJson(getGcmRegistrationDto(regId));
            String requestUrl = ApiConstants.BASE_URL+"/userservice/data/v1/entity/gcm-user?domainId=1";
            MakaanNetworkClient.getInstance().post(requestUrl, jObject, null, "Android");
        }catch(IllegalArgumentException e){} catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
