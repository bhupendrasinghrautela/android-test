package com.makaan.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by sunil on 09/12/15.
 */
public class DataProvider extends ContentProvider {

    public static final String SCHEMA = "content://";

    public static final String AUTHORITY = DataProvider.class.getName();
    public static final Uri SAVED_NOTIFICATIONS_URI = Uri.parse(SCHEMA + AUTHORITY + "/" + DatabaseHelper.TABLE_NOTIFICATIONS);

    private static final UriMatcher uriMatcher;
    private DatabaseHelper mDbHelper;

    private static final int NOTIFICATIONS = 1;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DatabaseHelper.TABLE_NOTIFICATIONS, NOTIFICATIONS);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = null;
        String where = null;
        switch (uriMatcher.match(uri)) {

            case NOTIFICATIONS:
                cursor = db.query(DatabaseHelper.TABLE_NOTIFICATIONS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case NOTIFICATIONS:
                return "vnd.android.cursor.dir/vnd.google.notifications";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = -1;
        Uri insertedUri = null;

        switch (uriMatcher.match(uri)) {

            case NOTIFICATIONS:
                rowId = db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, "", values);
                insertedUri = ContentUris.withAppendedId(SAVED_NOTIFICATIONS_URI, rowId);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (rowId >= 0 && insertedUri != null) {
            getContext().getContentResolver().notifyChange(insertedUri, null);
            return insertedUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowCount = 0;

        switch (uriMatcher.match(uri)) {
            case NOTIFICATIONS:
                rowCount = db.delete(DatabaseHelper.TABLE_NOTIFICATIONS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (rowCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowCount = 0;
        String where = null;
        switch (uriMatcher.match(uri)) {
            case NOTIFICATIONS:
                where = selection;
                rowCount = db.update(DatabaseHelper.TABLE_NOTIFICATIONS, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (rowCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowCount;
    }

}
