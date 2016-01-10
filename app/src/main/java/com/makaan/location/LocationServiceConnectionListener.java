package com.makaan.location;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Connection listeners indicating the state of
 * google location service connections
 * **/
public class LocationServiceConnectionListener implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private Context mContext;
    private MakaanLocationManager mMakaanLocationManager;

    public LocationServiceConnectionListener(Context context, MakaanLocationManager manager){
        this.mContext = context;
        this.mMakaanLocationManager = manager;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mMakaanLocationManager !=null) {
            mMakaanLocationManager.requestLocationUpdate();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}