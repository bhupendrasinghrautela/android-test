package com.makaan.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Helper class for managing location updates
 * Useful for all kinds of components like
 * Activities, fragments, receivers etc
 * */
public class MakaanLocationManager {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationServiceConnectionListener mServiceConnectionListener;
    private LocationListener mLocationListener;
    private Location mLastLocation;
    private LocationUpdateMode mLocationUpdateMode;
    private LocationStatus mLocationStatus;

    //Enum for location update frequency
    public enum LocationUpdateMode{
        ONCE, REGULAR
    }

    private enum LocationStatus{
        STOPPING, UPDATING
    }

    /**
     *Request a connection to google api client
     * @param ctx Context
     * @param serviceConnectionListener listeners for getting status
     * @param locationListener
     * @param locationUpdateMode
     * */
    public void connectLocationApiClient(Context ctx,
                                         LocationServiceConnectionListener serviceConnectionListener,
                                         LocationListener locationListener,
                                         LocationUpdateMode locationUpdateMode) {
        mServiceConnectionListener = serviceConnectionListener;
        mLocationListener = locationListener;
        mLocationUpdateMode = locationUpdateMode;
        createLocationRequest();
	    buildGoogleApiClient(ctx, serviceConnectionListener);
        mGoogleApiClient.connect();
    }

    /**
     * Stop location update and disconnect api client
     * @param ctx Context
     * */
    public void disconnectLocationApiClient(Context ctx) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(mServiceConnectionListener);
            mGoogleApiClient.unregisterConnectionFailedListener(mServiceConnectionListener);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Retrieves last updated location
     * @param context Context
     * */
    public Location getLastLocation(Context context) {
        if (mGoogleApiClient == null) {
            return null;
        }
        if (!mGoogleApiClient.isConnected() && mLastLocation!=null) {
            return mLastLocation;
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            return mLastLocation;
        }
    }

    /**
     * Requests location update on google api client is connected
     * */
	public void requestLocationUpdate(){

        if(mGoogleApiClient==null || !mGoogleApiClient.isConnected()){
            return;
        }

        if(mLocationStatus!=null && mLocationStatus== LocationStatus.UPDATING){
            return;
        }

        mLocationStatus = LocationStatus.UPDATING;

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLocationListener==null){
            return;
        }

        /**
         * Location is needed only once and last location is not null then send the last location
         * Otherwise call location updates.
         * */
        if(mLocationUpdateMode!=null && mLocationUpdateMode== LocationUpdateMode.ONCE  ) {
            if(mLastLocation!=null) {
                mLocationListener.onLocationChanged(mLastLocation);
                return;
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    public void stopLocationUpdate(LocationListener listener){

        mLocationStatus = LocationStatus.STOPPING;

        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected() && listener!=null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
        }
    }

    public LocationUpdateMode getLocationUpdateMode(){
        return mLocationUpdateMode;
    }

	private void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(10000);
	    mLocationRequest.setFastestInterval(5000);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	private synchronized void buildGoogleApiClient(Context ctx,
                                              LocationServiceConnectionListener listener) {
	    mGoogleApiClient = new GoogleApiClient.Builder(ctx)
		.addConnectionCallbacks(listener)
		.addOnConnectionFailedListener(listener)
		.addApi(LocationServices.API)
		.build();

	}
}
