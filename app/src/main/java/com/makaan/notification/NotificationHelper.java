package com.makaan.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.makaan.R;


/**
 * A Helper class with various utilities 
 * for creating notifications 
 * */

public class NotificationHelper{
	
    public static final int REQUEST_CODE_GCM = 100;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_TARGET_URL = "target_url";
    public static final String BUTTON_LABEL = "button_label";
	
	/**
	 * Enum for determining notification type
	 * */
	public enum NotificationType{

	}
	
	/**
	 * Enum for determining activity name
	 * */
	public enum ScreenType{
		LISTING(201), ENQUIRY(202), FAQ(203), NONE(204);

		int value;

		ScreenType(int code){
			value = code;
		}

		public int getValue() {
			return value;
		}

		public static ScreenType fromTypeId(int typeId) {
			for (ScreenType type : ScreenType.values()) {
				if (typeId==type.getValue()) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * Enum for determining activity name
	 * */
	public enum SortType{
		DATE_RECENT(301), DATE_OLDEST(302);

		int value;

		SortType(int code){
			value = code;
		}

		public int getValue() {
			return value;
		}

		public static SortType fromTypeId(int typeId) {
			for (SortType type : SortType.values()) {
				if (typeId==type.getValue()) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * A utility for creating a NotificationCompact.Builder
	 * @param context
	 * @param title
	 * @param plainText
	 * */
	public static NotificationCompat.Builder getNotificationBuilder(Context context, String title, String plainText){

		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.seek_thumb_normal);
		int height = (int) context.getResources().getDimension(R.dimen.notification_large_icon_height);
		int width = (int) context.getResources().getDimension(R.dimen.notification_large_icon_width);
		Bitmap icon = Bitmap.createScaledBitmap(largeIcon, height, width, false);
		largeIcon.recycle();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.seek_thumb_normal).setContentTitle(title)
				.setContentText(plainText).setWhen(System.currentTimeMillis()).setTicker(title)
				.setLargeIcon(icon)
				.setAutoCancel(true);

		return mBuilder;
	}

	
	/**
	 * common notify wrapper
	 * @param context
	 * @param attributes
	 * @param mBuilder
	 * */
	public static void notify(Context context, NotificationAttributes attributes, NotificationCompat.Builder mBuilder){
		
		if(context==null || mBuilder==null || attributes==null){
			return;
		}
		
		NotificationManager mNotifyMgr =
        		(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = null;
		
		if(attributes.getBitmap()==null){
			
			NotificationCompat.BigTextStyle notiStyle =
					new NotificationCompat.BigTextStyle().bigText(attributes.getMessage());
			notiStyle.setBigContentTitle(attributes.getTitle());
			
			notification = mBuilder.setStyle(notiStyle).build();
			
		}else{
			NotificationCompat.BigPictureStyle notiStyle =
	        		NotificationHelper.getNotificationBigPictureStyle(context, attributes.getBitmap());
			
			notiStyle.setBigContentTitle(attributes.getTitle());
			notiStyle.setSummaryText(attributes.getMessage());
			
			notification = mBuilder.setStyle(notiStyle).build();
		}
		
		if(notification!=null){
			notification.defaults |= Notification.DEFAULT_VIBRATE;
	        notification.defaults |= Notification.DEFAULT_SOUND;
			
			mNotifyMgr.notify(attributes.getNotificationId(), notification);
		}
	}
	
	
	/**
	 * A method for creating a BigPictureStyle for 
	 * expandable notifications
	 * @param context
	 * */
	public static NotificationCompat.BigPictureStyle getNotificationBigPictureStyle(Context context, Bitmap bitmap){
		if(bitmap==null){
			return null;
		}
		
		// Create the style object with BigPictureStyle subclass.
		NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
		
		// Add the big picture to the style.
		notiStyle.bigPicture(bitmap);
		
		return notiStyle;
				
	}

	/**
	 * Gets intent for launching an in-app activity
	 * @param context
	 * @param attributes {@link NotificationAttributes}
	 * */
	public static Intent getIntentFromAttribute(Context context, NotificationAttributes attributes){
		Intent resultIntent = null;

		ScreenType screenType =
				ScreenType.fromTypeId(attributes.getNotificationPayload().getScreenTypeId());
		if(screenType==null){
			return null;
		}



		return resultIntent;
	}



	
}