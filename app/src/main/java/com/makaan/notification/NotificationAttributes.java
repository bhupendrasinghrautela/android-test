package com.makaan.notification;

import android.graphics.Bitmap;

/**
 * Notification attribute model used for creating 
 * notifications
 * */
public class NotificationAttributes{
	
	private int notificationId = 1;
	private String title;
	private String message;
	private Bitmap bitmap;
	private boolean isRead;
	private long timestamp;
	
	private NotificationPayload notificationPayload;
	
	public int getNotificationId() {
		return notificationId;
	}
	
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String description) {
		this.message = description;
	}
	
	public NotificationPayload getNotificationPayload() {
		return notificationPayload;
	}

	public void setNotificationPayload(NotificationPayload notificationPayload) {
		this.notificationPayload = notificationPayload;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}