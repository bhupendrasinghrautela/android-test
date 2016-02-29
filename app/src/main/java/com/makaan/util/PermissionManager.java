package com.makaan.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.makaan.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>call {@link #isPermissionRequestRequired(Activity, String)}
 * to check whether particular permission should be requested before using the connected apis</p>
 * call {@link #begin()} to start adding permission requests which you want to request from user</p>
 * <p>then call {@link #addRequest(int)} or {@link #addRequest(String)} to add particular permission in the request list</p>
 * <p>and finally call {@link #request(Activity)} to show the request to the user in its mobile</p>
 */
public class PermissionManager {
    public static final int ACCOUNTS_REQUEST = 0x01;
    public static final int INTERNET_REQUEST = 0x02;
    public static final int CALL_PHONE_REQUEST = 0x04;
    public static final int FINE_LOCATION_REQUEST = 0x08;
    public static final int NETWORK_REQUEST = 0x10;
    public static final int READ_SMS_REQUEST = 0x20;
    public static final int RECEIVE_SMS_REQUEST = 0x40;
    public static final int GMS_REQUEST = 0x80;
    public static final int WAKE_LOCK_REQUEST = 0x100;
    public static final int MAKAAN_GMS_REQUEST = 0x200;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 0x400;
    public static final int READ_EXTERNAL_STORAGE_REQUEST = 0x800;

    static HashMap<String, PermissionRequest> permissions;
    static PermissionManager manager = new PermissionManager();
    ArrayList<String> permissionsToRequest = new ArrayList<>();

    private PermissionManager() {
    }

    public static final String REQUEST_GMS = "com.google.android.c2dm.permission.RECEIVE";

    public static final String REQUEST_MAKAAN_GMS = "com.makaan.permission.C2D_MESSAGE";

    static {
        permissions = new HashMap<>();
        permissions.put(android.Manifest.permission.GET_ACCOUNTS, new PermissionRequest(R.string.default_error_message, ACCOUNTS_REQUEST));
        permissions.put(android.Manifest.permission.INTERNET, new PermissionRequest(R.string.default_error_message, INTERNET_REQUEST));
        permissions.put(android.Manifest.permission.CALL_PHONE, new PermissionRequest(R.string.default_error_message, CALL_PHONE_REQUEST));
        permissions.put(android.Manifest.permission.ACCESS_FINE_LOCATION, new PermissionRequest(R.string.default_error_message, FINE_LOCATION_REQUEST));
        permissions.put(android.Manifest.permission.ACCESS_NETWORK_STATE, new PermissionRequest(R.string.default_error_message, NETWORK_REQUEST));
        permissions.put(android.Manifest.permission.READ_SMS, new PermissionRequest(R.string.default_error_message, READ_SMS_REQUEST));
        permissions.put(android.Manifest.permission.RECEIVE_SMS, new PermissionRequest(R.string.default_error_message, RECEIVE_SMS_REQUEST));
        permissions.put(REQUEST_GMS, new PermissionRequest(R.string.default_error_message, GMS_REQUEST));
        permissions.put(android.Manifest.permission.WAKE_LOCK, new PermissionRequest(R.string.default_error_message, WAKE_LOCK_REQUEST));
        permissions.put(REQUEST_MAKAAN_GMS, new PermissionRequest(R.string.default_error_message, MAKAAN_GMS_REQUEST));
        permissions.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionRequest(R.string.default_error_message, WRITE_EXTERNAL_STORAGE_REQUEST));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionRequest(R.string.default_error_message, READ_EXTERNAL_STORAGE_REQUEST));
        }
    }

    public static PermissionManager begin() {
        return manager.start();
    }

    private PermissionManager start() {
        permissionsToRequest.clear();
        return this;
    }

    public boolean request(Activity activity) {
        if (needPersionCheck()) {
            for (int i = 0; i < permissionsToRequest.size(); i++) {
                if (!isPermissionRequestRequired(activity, permissionsToRequest.get(i))) {
                    permissionsToRequest.remove(i);
                    i--;
                }
            }
            if (permissionsToRequest.size() > 0) {
                requestPermission(activity);
            }
        }
        return false;
    }

    private void requestPermission(final Activity activity) {
        ArrayList<String> nonSnackBarRequests = new ArrayList<>();
        for (int i = 0; i < permissionsToRequest.size(); i++) {
            boolean needToShowSnackBar = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsToRequest.get(i));
            if (!needToShowSnackBar) {
                nonSnackBarRequests.add(permissionsToRequest.get(i));
                permissionsToRequest.remove(i);
                i--;
            }
        }

        if (permissionsToRequest.size() > 0) {
            int code = 0;
            for (String permission : permissionsToRequest) {
                code |= permissions.get(permission).requestCode;
            }
            ActivityCompat.requestPermissions(activity,
                    permissionsToRequest.toArray(new String[0]), code);
        }

        if (nonSnackBarRequests.size() > 0) {
            int code = 0;
            for (String permission : nonSnackBarRequests) {
                code |= permissions.get(permission).requestCode;
            }
            ActivityCompat.requestPermissions(activity,
                    nonSnackBarRequests.toArray(new String[0]), code);
        }

    }

    public PermissionManager addRequest(String permission) {
        if (permissions.containsKey(permission)) {
            permissionsToRequest.add(permission);
        }
        return this;
    }

    public PermissionManager addRequest(int code) {
        switch (code) {
            case ACCOUNTS_REQUEST:
                permissionsToRequest.add(Manifest.permission.GET_ACCOUNTS);
                break;
            case INTERNET_REQUEST:
                permissionsToRequest.add(Manifest.permission.INTERNET);
                break;
            case CALL_PHONE_REQUEST:
                permissionsToRequest.add(Manifest.permission.CALL_PHONE);
                break;
            case FINE_LOCATION_REQUEST:
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case NETWORK_REQUEST:
                permissionsToRequest.add(Manifest.permission.ACCESS_NETWORK_STATE);
                break;
            case READ_SMS_REQUEST:
                permissionsToRequest.add(Manifest.permission.READ_SMS);
                break;
            case RECEIVE_SMS_REQUEST:
                permissionsToRequest.add(Manifest.permission.RECEIVE_SMS);
                break;
            case GMS_REQUEST:
                permissionsToRequest.add(REQUEST_GMS);
                break;
            case WAKE_LOCK_REQUEST:
                permissionsToRequest.add(Manifest.permission.WAKE_LOCK);
                break;
            case MAKAAN_GMS_REQUEST:
                permissionsToRequest.add(REQUEST_MAKAAN_GMS);
                break;
            case WRITE_EXTERNAL_STORAGE_REQUEST:
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case READ_EXTERNAL_STORAGE_REQUEST:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
        }
        return this;
    }

    public boolean needPersionCheck() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isPermissionRequestRequired(Activity activity, String permission) {
        return permissions.containsKey(permission) && ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;
    }

    static class PermissionRequest {
        int messageResourseId;
        int requestCode;

        public PermissionRequest(int messageResourseId, int requestCode) {
            this.messageResourseId = messageResourseId;
            this.requestCode = requestCode;
        }
    }
}
