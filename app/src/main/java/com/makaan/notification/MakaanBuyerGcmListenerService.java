package com.makaan.notification;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.gcm.GcmListenerService;
import com.makaan.database.NotificationDbHelper;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.VersionUpdate;
import com.makaan.util.CommonPreference;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonParser;

/**
 * Created by sunil on 29/02/16.
 */
public class MakaanBuyerGcmListenerService extends GcmListenerService {

    private static final String TAG = MakaanBuyerGcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.e("Gcm","receive");

        String message = "";
        String title = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            message = data.getString("message");
            title = data.getString("title");

        } else {
            message = data.getString("message", "");
            title = data.getString("title", "Makaan");
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionUpdate versionUpdate = (VersionUpdate) JsonParser.parseJson(CommonPreference.getMandatoryVersion(this),VersionUpdate.class);
            if (versionUpdate!=null && pInfo.versionCode >= versionUpdate.getMandatoryVersionCode() ) {
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
                    generateNotification(getApplicationContext(), title.toLowerCase(), message.toLowerCase(), data);
                }
            }
        }catch (NameNotFoundException e){}
    }


    private void generateNotification(Context context, String title, String message, Bundle params){
        NotificationAttributes attributes = new NotificationAttributes();
        attributes.setTitle(title);
        attributes.setMessage(message);

        NotificationPayload payload = parsePayload(params);

        if (payload == null) {
            return;
        }

        attributes.setNotificationId(payload.getNotificationId());
        attributes.setNotificationPayload(payload);

        if(TextUtils.isEmpty(attributes.getNotificationPayload().getImageUrl())){
            createNotification(context, attributes);
        }else{
            fetchBigImageAndCreateNotification(context, attributes);
        }
    }

    private void createNotification(Context context, NotificationAttributes attributes){
        try{
            MakaanNotification notification = NotificationFactory.getNotification(attributes);
            if(notification!=null) {
                notification.createNotification(context, attributes);
                storeNotification(context, attributes);
            }
        }catch(Exception e){
            //do nothing as there might be some issue with the data
        }
    }

    private void fetchBigImageAndCreateNotification(final Context context, final NotificationAttributes attributes){

        Request<?> newRequest =
                new ImageRequest(attributes.getNotificationPayload().getImageUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        attributes.setBitmap(response);
                        createNotification(context, attributes);
                    }
                }, 0, 0, Bitmap.Config.RGB_565,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                createNotification(context, attributes);
                            }
                        });

        // Adding request to request queue
        MakaanNetworkClient.getInstance().getRequestQueue().add(newRequest);
    }

    private NotificationPayload parsePayload(Bundle params){
        NotificationPayload payload =null;
        try{
            payload = (NotificationPayload) JsonParser.parseJson(
                    CommonUtil.bundletoJSONObject(params).toString(), NotificationPayload.class);
            return payload;
        }catch(Exception e){
            CommonUtil.TLog("Exception e : " + e);
        }
        return payload;
    }



    private void storeNotification(Context context, NotificationAttributes attributes){
        attributes.setTimestamp(System.currentTimeMillis());
        NotificationDbHelper.insertNotification(context,attributes);
    }
}
