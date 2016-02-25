package com.makaan.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/**
 * Created by sunil on 25/02/16.
 */
public class BrowserNotification implements MakaanNotification {
    @Override
    public void createNotification(Context context, NotificationAttributes attributes) {
        String title = attributes.getTitle();
        String message = attributes.getMessage();

        NotificationCompat.Builder mBuilder =
                NotificationHelper.getNotificationBuilder(context, title, message);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(attributes.getNotificationPayload().getTargetUrl()));

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, NotificationHelper.REQUEST_CODE_GCM, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationHelper.notify(context, attributes, mBuilder);
    }
}
