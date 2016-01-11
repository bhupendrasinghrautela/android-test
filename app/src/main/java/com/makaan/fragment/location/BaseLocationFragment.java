package com.makaan.fragment.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.location.LocationListener;
import com.makaan.location.LocationServiceConnectionListener;
import com.makaan.location.MakaanLocationManager;

/**
 * Created by sunil on 26/12/15.
 */
public class BaseLocationFragment extends Fragment implements LocationListener {

    private MakaanLocationManager mMakaanLocationManager;
    private LocationManager mLocationManager;


    /**
     * starts location update
     * @param mode location update frequency mode
     * */
    protected void connectLocationApiClient(MakaanLocationManager.LocationUpdateMode mode) {
        if(mMakaanLocationManager ==null){
            mMakaanLocationManager = new MakaanLocationManager();
        }
        LocationServiceConnectionListener listener =
                new LocationServiceConnectionListener(getActivity(), mMakaanLocationManager);

        mMakaanLocationManager.connectLocationApiClient(getActivity(), listener, this, mode);
    }

    /**
     * Starts location update
     * */
    protected void startLocationUpdate(){
        if(mMakaanLocationManager !=null){
            mMakaanLocationManager.requestLocationUpdate();
        }
    }


    /**
     * Stops location update
     * @param listener a location listener
     * */
    protected void stopLocationUpdate(LocationListener listener){
        if(mMakaanLocationManager !=null) {
            mMakaanLocationManager.stopLocationUpdate(listener);
        }
    }

    /**
     * get last location
     * @return Location
     * */
    public Location getLastLocation(){
        if(mMakaanLocationManager ==null){
            return null;
        }

        return mMakaanLocationManager.getLastLocation(getActivity());

    }

    /*
    disconnect the Location client
     */
    protected void disconnectLocationApiClient() {
        if (mMakaanLocationManager != null) {
            mMakaanLocationManager.disconnectLocationApiClient(getActivity());
        }
    }


    protected boolean checkLocationSettings() {
        try {
            if (!getLocationAvailabilty()) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                EnableLocationSettingsDialogFragment enableLocationSettingsDialogFragment =
                        EnableLocationSettingsDialogFragment.newInstance(getActivity());
                enableLocationSettingsDialogFragment.show(fragmentManager, "ENABLE_LOCATION");
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    public boolean getLocationAvailabilty(){
        if(mLocationManager == null){
            mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            startLocationUpdate();
        }catch(Exception e){}
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            stopLocationUpdate(BaseLocationFragment.this);
        }catch(Exception e){}
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        if(mMakaanLocationManager !=null) {
            mMakaanLocationManager.disconnectLocationApiClient(getActivity());
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMakaanLocationManager.getLocationUpdateMode()
                == MakaanLocationManager.LocationUpdateMode.ONCE) {
            try {
                stopLocationUpdate(BaseLocationFragment.this);
            }catch(Exception e){}
        }
    }
}
