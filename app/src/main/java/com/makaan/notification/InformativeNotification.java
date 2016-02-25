package com.makaan.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.makaan.activity.HomeActivity;

/**
 * Created by sunil on 25/02/16.
 */
public class InformativeNotification implements MakaanNotification {
    @Override
    public void createNotification(Context context, NotificationAttributes attributes) {
        String title = attributes.getTitle();
        String message = attributes.getMessage();

        NotificationCompat.Builder mBuilder =
                NotificationHelper.getNotificationBuilder(context, title, message);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(NotificationHelper.EXTRA_TITLE, title);
        resultIntent.putExtra(NotificationHelper.EXTRA_MESSAGE, message);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, NotificationHelper.REQUEST_CODE_GCM, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationHelper.notify(context, attributes, mBuilder);

    }
}
