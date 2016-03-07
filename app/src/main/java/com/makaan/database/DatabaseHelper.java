package com.makaan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunil on 09/12/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "makaan_buyer.db";

    // Increment db version on any change to schema.
    private static final int DB_CURRENT_VERSION = 1;

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_NOTIFICATION_INSERT_TRIGGER = "table_notification_list_insert_triger";



    private static final String SQL_CREATE_NOTIFICATION_LIST =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " ("
                    + NotificationAttributeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + NotificationAttributeColumns.NOTIFICATION_ID + " INTEGER UNIQUE ON CONFLICT REPLACE, "
                    + NotificationAttributeColumns.TITLE + " TEXT, "
                    + NotificationAttributeColumns.MESSAGE + " TEXT, "
                    + NotificationAttributeColumns.CAMPAIGN_NAME + " TEXT, "
                    + NotificationAttributeColumns.SCREEN_TYPE_ID + " INTEGER, "
                    + NotificationAttributeColumns.NOTIFICATION_TYPE_ID + " INTEGER, "
                    + NotificationAttributeColumns.IMAGE_URL + " TEXT, "
                    + NotificationAttributeColumns.TARGET_URL + " TEXT, "
                    + NotificationAttributeColumns.SORT_ORDER + " INTEGER, "
                    + NotificationAttributeColumns.CITY_ID + " INTEGER, "
                    + NotificationAttributeColumns.LOCALITY_ID + " INTEGER, "
                    + NotificationAttributeColumns.PROJECT_ID + " INTEGER, "
                    + NotificationAttributeColumns.LISTING_ID + " INTEGER, "
                    + NotificationAttributeColumns.SERP_FILTER_URL + " TEXT, "
                    + NotificationAttributeColumns.READ + " INTEGER, "
                    + NotificationAttributeColumns.TIMESTAMP + " INTEGER); ";

    private static final String SQL_INSERT_TRIGGER_NOTIFICATION_LIST =
            "CREATE TRIGGER " + TABLE_NOTIFICATION_INSERT_TRIGGER + " AFTER INSERT ON "  + TABLE_NOTIFICATIONS
                    + " BEGIN DELETE FROM " + TABLE_NOTIFICATIONS
                    + " where " + NotificationAttributeColumns._ID
                    + " NOT IN (SELECT " + NotificationAttributeColumns._ID
                    + " from " + TABLE_NOTIFICATIONS
                    + " ORDER BY " + NotificationAttributeColumns.TIMESTAMP
                    + " DESC LIMIT " + NotificationDbHelper.NOTIFICATION_LIST_MAX_LENGTH+"); END";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NOTIFICATION_LIST);
        db.execSQL(SQL_INSERT_TRIGGER_NOTIFICATION_LIST);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateAllTables(db);
    }

    private void recreateAllTables(SQLiteDatabase db) {
        // for future use.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);

        onCreate(db);
    }

    public static class NotificationAttributeColumns{
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String MESSAGE = "message";
        public static final String NOTIFICATION_ID = "notificationId";
        public static final String CAMPAIGN_NAME = "campaignName";
        public static final String NOTIFICATION_TYPE_ID = "notificationTypeId";
        public static final String SCREEN_TYPE_ID = "screenTypeId";
        public static final String SORT_ORDER = "sortOrder";
        public static final String IMAGE_URL = "imageUrl";
        public static final String TARGET_URL = "targetUrl";
        public static final String CITY_ID = "cityId";
        public static final String LOCALITY_ID = "localityId";
        public static final String PROJECT_ID = "projectId";
        public static final String LISTING_ID = "listingId";
        public static final String SERP_FILTER_URL = "serpFilterUrl";
        public static final String TIMESTAMP = "timeStamp";
        public static final String READ = "read";
    }

}
