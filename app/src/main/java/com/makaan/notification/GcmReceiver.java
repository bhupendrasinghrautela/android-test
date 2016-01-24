package com.makaan.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonParser;

public class GcmReceiver extends WakefulBroadcastReceiver {
	
	private static final String TAG = GcmReceiver.class.getSimpleName();
    
    @SuppressLint("NewApi")
	@Override
    public void onReceive(Context context, Intent intent) {
		try {
			
			//if(!Preferences.isPushNotificationsOn(context)) return;
			
			if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
				// Registration complete
				
			}else if(intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")){
				//message received
				
				Bundle extras = intent.getExtras();
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
				String messageType = gcm.getMessageType(intent);
				if (!extras.isEmpty()) {
					if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
						String message = "";
						String title = "";
						if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
							message = extras.getString("message");
							title = extras.getString("title");
							
						} else {
							message = extras.getString("message", "");
							title = extras.getString("title", "Makaan");
						}
						generateNotification(context, title, message, extras);
					}
				}
			}
			
			// Release wakelock
			completeWakefulIntent(intent);
		} catch (Exception e) {}
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
		storeNotification(context, attributes);



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
			}
    	}catch(Exception e){
    		//do nothing as there might be some issue with the data
    	}
    }
    
    private void fetchBigImageAndCreateNotification(final Context context, final NotificationAttributes attributes){
    	
        Request<?> newRequest =
                new ImageRequest(attributes.getNotificationPayload().getImageUrl(), new Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                    	attributes.setBitmap(response);
                    	createNotification(context, attributes);
                    }
                }, 0, 0, Config.RGB_565,
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    	createNotification(context, attributes);
                    }
                });
        
        // Adding request to request queue
        //MakaanNetworkClient.getInstance().(newRequest, TAG);
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
	}

}