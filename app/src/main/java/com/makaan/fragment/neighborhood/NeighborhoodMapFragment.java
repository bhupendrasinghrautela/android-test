package com.makaan.fragment.neighborhood;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.makaan.R;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.amenity.Amenity;
import com.makaan.response.amenity.AmenityCluster;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by sunil on 24/01/16.
 */
public class NeighborhoodMapFragment extends MakaanBaseFragment implements NeighborhoodCategoryAdapter.CategoryClickListener {

    private GoogleMap mPropertyMap;
    private List<Marker> mAllMarkers = new ArrayList<Marker>();
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private List<AmenityCluster> mAmenityClusters = new ArrayList<>();
    private AmenityCluster mSelectedAmenityCluster;
    private SelectedMarker mSelectedMarker = new SelectedMarker();
    private NeighborhoodCategoryAdapter mNeighborhoodCategoryAdapter;
    private LinearLayoutManager mLayoutManager;

    @Bind(R.id.neighborhood_map_view)
    MapView mMapView;

    @Bind(R.id.neighborhood_category)
    RecyclerView mNeighborhoodCategoryView;

    @Override
    protected int getContentViewId() {
        return R.layout.neighborhood_map_layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mNeighborhoodCategoryAdapter = new NeighborhoodCategoryAdapter(getActivity(), this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);int status =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if(status == ConnectionResult.SUCCESS) {
            initMap(savedInstanceState);

        }else{
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), status);
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void setData(List<AmenityCluster> amenityClusters){
        for(AmenityCluster cluster : amenityClusters){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }
    }

    private void setCategoryPosition(int position){
        if(mAmenityClusters == null || mAmenityClusters.isEmpty() || mPropertyMap == null){
            return;
        }
        populateMarker(mAmenityClusters.get(position));
    }

/*    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(amenityGetEvent == null || amenityGetEvent.amenityClusters == null){
            return;
        }
        //mAmenityClusters = amenityGetEvent.amenityClusters;
        for(AmenityCluster cluster : amenityGetEvent.amenityClusters){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }
        mNeighborhoodCategoryAdapter.setData(mAmenityClusters);
        setCategoryPosition(0);
    }*/

    private void initMap(@Nullable Bundle savedInstanceState) {

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mPropertyMap = mMapView.getMap();

        if(mPropertyMap==null){
            return;
        }
        mPropertyMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectMarker(marker);
                return false;
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mNeighborhoodCategoryView.setLayoutManager(mLayoutManager);
        mNeighborhoodCategoryView.setAdapter(mNeighborhoodCategoryAdapter);
        mNeighborhoodCategoryAdapter.setData(mAmenityClusters);
        setCategoryPosition(0);
    }

    private void populateMarker(AmenityCluster amenityCluster){
        mSelectedAmenityCluster = amenityCluster;
        List<Amenity> amenities = amenityCluster.cluster;
        mAllMarkers.clear();
        clearMap();


        mLatLngBoundsBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < amenities.size(); i++) {
            double lat = amenities.get(i).lat;
            double lng = amenities.get(i).lon;
            addMarker(mLatLngBoundsBuilder, lat, lng, amenities.get(i));
        }

        animateToLocation(mLatLngBoundsBuilder);
    }

    private void selectMarker(Marker marker){
        Bitmap markerBitmap;
        if(null!=mSelectedMarker.amenity) {
            markerBitmap = getMarkerBitmap(false, mSelectedMarker.amenity);
            setMarkerIcon(mSelectedMarker.marker, markerBitmap);
        }


        mSelectedMarker.marker = marker;
        List<Amenity> mAmenities = mSelectedAmenityCluster.cluster;
        for (int i = 0; i < mAmenities.size(); i++) {
            if (marker.getTitle().equalsIgnoreCase(String.valueOf(mAmenities.get(i).name))) {
                mSelectedMarker.amenity = mAmenities.get(i);
                break;
            }
        }

        markerBitmap = getMarkerBitmap(true, mSelectedMarker.amenity);
        setMarkerIcon(mSelectedMarker.marker, markerBitmap);
    }


    private void clearMap(){
        if(mPropertyMap!=null){
            mPropertyMap.clear();
        }
    }

    private void addMarker(LatLngBounds.Builder latLngBoundsBuilder,
                           double lat, double lng, Amenity amenity) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(String.valueOf(amenity.name))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(getMarkerBitmap(false, amenity)));

        Marker marker = mPropertyMap.addMarker(markerOptions);
        latLngBoundsBuilder.include(new LatLng(lat, lng));
        mAllMarkers.add(marker);
    }

    private void animateToLocation(final LatLngBounds.Builder latLngBoundsBuilder) {

        if(mMapView==null){
            return;
        }

        //TODO calculate padding
        final int mapPadding = 180;

        final LatLngBounds currentLatLngBounds = latLngBoundsBuilder.build();
        try {
            mPropertyMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            currentLatLngBounds, mapPadding));

        } catch (Exception e) {
            if (mMapView.getViewTreeObserver().isAlive()) {
                mMapView.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {

                            @SuppressWarnings("deprecation")
                            @SuppressLint("NewApi")
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                try {
                                    mPropertyMap.animateCamera(
                                            CameraUpdateFactory.newLatLngBounds(
                                                    currentLatLngBounds, mapPadding));
                                } catch (Exception e) {}
                            }
                        });
            }
        }
    }

    private void setMarkerIcon(Marker marker, Bitmap icon){
        try {
            if (marker != null) {
                marker.hideInfoWindow();
                marker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(icon));
            }
        }catch(Exception e){}
    }

    private Bitmap getMarkerBitmap(boolean isSelected, Amenity amenity){
        IconGenerator iconGenerator = new IconGenerator(getActivity());
        iconGenerator.setStyle(isSelected ? IconGenerator.STYLE_RED : IconGenerator.STYLE_WHITE);
        Bitmap markerBitmap = iconGenerator.makeIcon(amenity.displayDistance);
        return markerBitmap;
    }

    @Override
    public void onItemClick(int position, View v) {
        populateMarker(mAmenityClusters.get(position));
    }

    private class SelectedMarker {
        Marker marker;
        Amenity amenity;
    }

}
