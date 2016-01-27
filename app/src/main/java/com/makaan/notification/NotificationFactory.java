package com.makaan.notification;



/**
 * A factory for creating notifications
 * */
public class NotificationFactory{
	
	/**
	 * Return appropriate notifications based on attributes 
	 * */
	public static MakaanNotification getNotification(NotificationAttributes attributes){
		
		MakaanNotification notification = new ActivityLauncherNotification();

		return notification;
	}
}