package com.makaan.notification;

import android.content.Context;


/**
 * An interface for building notifications
 * */
public interface MakaanNotification {
	void createNotification(Context context, NotificationAttributes attributes);
}