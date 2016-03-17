package com.makaan.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


/**
 * A concrete implementation of {@link MakaanNotification}
 * 
 * The intent for this type of notification will launch an intermediate
 * NotificationReader activity with supplied data
 * */
public class ScreenLauncherNotification implements MakaanNotification{


	@Override
	public void createNotification(Context context, NotificationAttributes attributes) {
		String title = attributes.getTitle();
		String message = attributes.getMessage();
		
		NotificationCompat.Builder mBuilder =
				NotificationHelper.getNotificationBuilder(context, title, message);
		
		Intent resultIntent = buildResultIntent(context, attributes);
		if(resultIntent==null){
			//Don't show any notification, incomplete info
			return;
		}
		
/*		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
				Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_NO_ANIMATION);*/
		
        resultIntent.putExtra(NotificationHelper.EXTRA_TITLE, title);
        resultIntent.putExtra(NotificationHelper.EXTRA_MESSAGE, message);
        PendingIntent resultPendingIntent =
        		PendingIntent.getActivity(context, NotificationHelper.REQUEST_CODE_GCM, resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
        
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationHelper.notify(context, attributes, mBuilder);
	}
	
	private Intent buildResultIntent(Context context, NotificationAttributes attributes){
		Intent resultIntent = NotificationHelper.getIntentFromAttribute(context, attributes);
		return resultIntent;
	}
	




}