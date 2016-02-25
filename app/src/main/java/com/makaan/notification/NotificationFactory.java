package com.makaan.notification;



/**
 * A factory for creating notifications
 * */
public class NotificationFactory{
	
	/**
	 * Return appropriate notifications based on attributes 
	 * */
	public static MakaanNotification getNotification(NotificationAttributes attributes){
		
		MakaanNotification notification;
		NotificationHelper.NotificationType type =
				NotificationHelper.NotificationType.fromTypeId(attributes.getNotificationPayload().getNotificationTypeId());

		if(type==null){
			return null;
		}

		switch(type){
			case BROWSER:
				notification = new BrowserNotification();
				break;
			case SCREEN_LAUNCHER:
				notification = new ScreenLauncherNotification();
				break;
			case NONE:
				notification = new InformativeNotification();
				break;
			default:
				notification = new InformativeNotification();
				break;

		}
		return notification;
	}
}