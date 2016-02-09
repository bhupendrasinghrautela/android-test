package com.makaan.fragment.listing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.ui.listing.ListingViewPager;
import com.makaan.ui.listing.OnListingPagerChangeListener;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingData;
import com.makaan.util.StringUtil;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunil on 03/01/16.
 */
public class SerpMapFragment extends MakaanBaseFragment {

    private List<Marker> mAllMarkers = new ArrayList<Marker>();
    private SelectedObject mSelectedObject = new SelectedObject();
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private List<Listing> mListings;
    private GoogleMap mPropertyMap;

    @Bind(R.id.serp_map_listing_viewpager)
    ListingViewPager mProjectViewPager;

    @Bind(R.id.serp_map_view)
    MapView mMapView;

    @Override
    protected int getContentViewId() {
        return R.layout.serp_map_layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(status == ConnectionResult.SUCCESS) {
            initMap(savedInstanceState);
            initPager();
            if(mListings != null) {
                populateMarker(mListings);
                mProjectViewPager.setData(mListings);
                // display first property be default
                if(mAllMarkers.size() > 0) {
                    displayProject(mAllMarkers.get(0));
                }
            }

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

    public void setData(ArrayList<Listing>listings){
        if(mListings == null) {
            mListings = new ArrayList<Listing>();
        } else {
            mListings.clear();
        }
        mListings.addAll(listings);
        if(mProjectViewPager != null) {
            populateMarker(mListings);
            mProjectViewPager.setData(mListings);
        }
    }

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
                displayProject(marker);
                return false;
            }
        });

        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    unselectMarker();
                }
                return false;
            }
        });
    }

    private void initPager(){

        mProjectViewPager.bindView();
        mProjectViewPager.addOnPageChangeListener(new OnListingPagerChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mSelectedObject.marker == null || mSelectedObject.listing == null) {
                    return;
                }

                setMarkerIcon(mSelectedObject.marker,
                        getMarkerBitmap(false, mSelectedObject.listing));

                Listing listing = mListings.get(position);
                LatLng projectLocation = new LatLng(listing.latitude, listing.longitude);
                mPropertyMap.animateCamera(CameraUpdateFactory.newLatLng(projectLocation));

                Marker marker = mAllMarkers.get(position);
                if (position > 0) {
                    setMarkerIcon(mAllMarkers.get(position - 1),
                            getMarkerBitmap(false, mListings.get(position - 1)));
                }

                if ((position + 1) < mAllMarkers.size()) {
                    setMarkerIcon(mAllMarkers.get(position + 1),
                            getMarkerBitmap(false, mListings.get(position + 1)));
                }

                setMarkerIcon(marker, getMarkerBitmap(true, mListings.get(position)));

                mSelectedObject.marker = marker;
                mSelectedObject.listing = listing;
            }
        });
    }

    private void populateMarker(List<Listing> listings){

        mAllMarkers.clear();
        clearMap();

        if (mListings.isEmpty()) {
            return;
        }

        mLatLngBoundsBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < listings.size(); i++) {
            double lat = listings.get(i).latitude;
            double lng = listings.get(i).longitude;
            if (lat != 0.0 && !Double.isNaN(lat) && lng != 0.0 && !Double.isNaN(lng)) {
                addMarker(mLatLngBoundsBuilder, lat, lng, mListings.get(i));
            } else {
                // remove listing which cannot be represented on the map
                listings.remove(i);
                i--;
            }
        }

        animateToLocation(mLatLngBoundsBuilder);
    }

    private void displayProject(Marker marker){
        mSelectedObject.marker = marker;

        for (int i = 0; i < mListings.size(); i++) {
            if (marker.getTitle().equalsIgnoreCase(String.valueOf(mListings.get(i).lisitingId))) {
                mProjectViewPager.setCurrentItem(i, true);
                mSelectedObject.listing = mListings.get(i);

            }
        }
        selectMarker();
    }


    private boolean unselectMarker(){
        if(mProjectViewPager.getVisibility()==View.VISIBLE){
            mProjectViewPager.hide();
            Bitmap markerBitmap = getMarkerBitmap(false, mSelectedObject.listing);
            setMarkerIcon(mSelectedObject.marker, markerBitmap);
            return true;
        }
        return false;
    }

    private boolean selectMarker(){
        if(mProjectViewPager.getVisibility()!=View.VISIBLE){
            mProjectViewPager.show();
            Bitmap markerBitmap = getMarkerBitmap(true, mSelectedObject.listing);
            setMarkerIcon(mSelectedObject.marker, markerBitmap);
            return true;
        }
        return false;
    }

    private void clearMap(){
        if(mPropertyMap!=null){
            mPropertyMap.clear();
        }
        mSelectedObject.marker = null;
    }

    private void addMarker(LatLngBounds.Builder latLngBoundsBuilder,
                           double lat, double lng, Listing listing) {

        IconGenerator iconGenerator = new IconGenerator(getActivity());
        iconGenerator.setStyle(IconGenerator.STYLE_RED);
        Bitmap markerBitmap =
                iconGenerator.makeIcon(String.valueOf(listing.bedrooms));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(String.valueOf(listing.lisitingId))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(markerBitmap));

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

        final LatLngBounds current_LatLngBounds = latLngBoundsBuilder.build();
            try {
                mPropertyMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                current_LatLngBounds, mapPadding));

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
                                                current_LatLngBounds, mapPadding));
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

    private Bitmap getMarkerBitmap(boolean isSelected, Listing listing){
        IconGenerator iconGenerator = new IconGenerator(getActivity());
        iconGenerator.setStyle(isSelected ? IconGenerator.STYLE_ORANGE : IconGenerator.STYLE_RED);
        Bitmap markerBitmap = iconGenerator.makeIcon(String.valueOf(listing.bedrooms));
        return markerBitmap;
    }

    private class SelectedObject {
        Marker marker;
        Listing listing;
    }

}
