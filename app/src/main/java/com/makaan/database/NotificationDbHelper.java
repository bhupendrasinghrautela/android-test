package com.makaan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.makaan.notification.NotificationAttributes;
import com.makaan.notification.NotificationPayload;
import com.makaan.database.DatabaseHelper.NotificationAttributeColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 28/12/15.
 */
public class NotificationDbHelper {
    public static int NOTIFICATION_LIST_MAX_LENGTH = 20;
    /**
     * Gets list of notifications stored in DB
     *
     * @param cursor Cursor
     * @return list of {@link NotificationAttributes}
     */
    public static List<NotificationAttributes> getNotificationList(Cursor cursor) {
//        Cursor cursor = ctx.getContentResolver().query(
//                PropertyProvider.SAVED_NOTIFICATIONS_URI, null, null, null,
//                NotificationAttributeColumns.TIMESTAMP + " DESC");

        if (cursor == null) {
            return null;
        }

        if (cursor != null) {

            List<NotificationAttributes> attributesList = new ArrayList<NotificationAttributes>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    NotificationAttributes attributes = new NotificationAttributes();

                    attributes.setNotificationId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.NOTIFICATION_ID)));
                    attributes.setTitle(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.TITLE)));
                    attributes.setMessage(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.MESSAGE)));
                    attributes.setIsRead((cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.READ)) == 0 ? false : true));
                    attributes.setTimestamp(cursor.getLong(cursor.getColumnIndex(NotificationAttributeColumns.TIMESTAMP)));

                    NotificationPayload payload = new NotificationPayload();

                    payload.setCampaignName(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.CAMPAIGN_NAME)));
                    payload.setScreenTypeId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.SCREEN_TYPE_ID)));
                    payload.setNotificationTypeId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.NOTIFICATION_TYPE_ID)));
                    payload.setImageUrl(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.IMAGE_URL)));
                    payload.setTargetUrl(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.TARGET_URL)));

                    payload.setCityId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.CITY_ID)));
                    payload.setLocalityId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.LOCALITY_ID)));
                    payload.setProjectId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.PROJECT_ID)));
                    payload.setListingId(cursor.getInt(cursor.getColumnIndex(NotificationAttributeColumns.LISTING_ID)));
                    payload.setSerpFilterUrl(cursor.getString(cursor.getColumnIndex(NotificationAttributeColumns.SERP_FILTER_URL)));

                    attributes.setNotificationPayload(payload);

                    attributesList.add(attributes);
                    cursor.moveToNext();
                }
//                cursor.close();

            }
            return attributesList;
        }
        return null;
    }

    /**
     * Insert a notification into the DB
     *
     * @param ctx                    Context
     * @param notificationAttributes {@link NotificationAttributes}
     * @return uri of inserted notification
     */
    public static Uri insertNotification(Context ctx, NotificationAttributes notificationAttributes) {
        ContentValues values = getContentValues(notificationAttributes);
        Uri searchUri = ctx.getContentResolver().insert(DataProvider.SAVED_NOTIFICATIONS_URI, values);
        return searchUri;
    }


    /**
     * Deletes leads in the db
     *
     * @param context Context
     * */
    public static int deleteNotification(Context context, int notificationId){
        String whereClause = NotificationAttributeColumns.NOTIFICATION_ID + "=?";
        String[] args = new String[]{String.valueOf(notificationId)};

        return context.getContentResolver().delete(DataProvider.SAVED_NOTIFICATIONS_URI, whereClause, args);
    }

    /**
     * Marks a notification as read
     *
     * @param ctx            Context
     * @param notificationId
     * @return int number of updated rows
     */
    public static int markNotificationRead(Context ctx, int notificationId) {

        ContentValues values = new ContentValues();
        values.put(NotificationAttributeColumns.READ, 1);
        String whereClause = NotificationAttributeColumns.NOTIFICATION_ID + "=?";
        String[] args = new String[]{String.valueOf(notificationId)};

        return ctx.getContentResolver().update(DataProvider.SAVED_NOTIFICATIONS_URI, values, whereClause, args);

    }
    /**
     * Get Unread Notification Count
     */
    public static int getUnreadNotificationsCount(Context context) {
        Cursor cursor = context.getContentResolver().query(DataProvider.SAVED_NOTIFICATIONS_URI, null, NotificationAttributeColumns.READ + "=0",
                null, null);

        int count = cursor.getCount();
        return count;
    }

    private static ContentValues getContentValues(NotificationAttributes attributes) {
        ContentValues values = new ContentValues();
        values.put(NotificationAttributeColumns.TITLE, attributes.getTitle());
        values.put(NotificationAttributeColumns.MESSAGE, attributes.getMessage());
        values.put(NotificationAttributeColumns.NOTIFICATION_ID, attributes.getNotificationId());
        values.put(NotificationAttributeColumns.TIMESTAMP, attributes.getTimestamp());
        values.put(NotificationAttributeColumns.READ, attributes.isRead() ? 1 : 0);

        NotificationPayload payload = attributes.getNotificationPayload();

        if (payload == null) {
            return null;
        }

        values.put(NotificationAttributeColumns.CAMPAIGN_NAME, payload.getCampaignName());
        values.put(NotificationAttributeColumns.SCREEN_TYPE_ID, payload.getScreenTypeId());
        values.put(NotificationAttributeColumns.NOTIFICATION_TYPE_ID, payload.getNotificationTypeId());
        values.put(NotificationAttributeColumns.IMAGE_URL, payload.getImageUrl());
        values.put(NotificationAttributeColumns.TARGET_URL, payload.getTargetUrl());

        values.put(NotificationAttributeColumns.CITY_ID, payload.getCityId());
        values.put(NotificationAttributeColumns.LISTING_ID, payload.getListingId());
        values.put(NotificationAttributeColumns.PROJECT_ID, payload.getProjectId());
        values.put(NotificationAttributeColumns.LISTING_ID, payload.getListingId());
        values.put(NotificationAttributeColumns.SERP_FILTER_URL, payload.getSerpFilterUrl());

        return values;
    }

}
