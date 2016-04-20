package com.makaan.fragment.neighborhood;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.response.amenity.Amenity;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.service.AmenityService;
import com.makaan.service.MakaanServiceFactory;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 24/01/16.
 */
public class NeighborhoodMapFragment extends MakaanBaseFragment implements NeighborhoodCategoryAdapter.CategoryClickListener {

    @Bind(R.id.neighborhood_map_view)
    MapView mMapView;

    @Bind(R.id.neighborhood_category)
    RecyclerView mNeighborhoodCategoryView;

    private GoogleMap mPropertyMap;
    private List<Marker> mAllMarkers = new ArrayList<Marker>();
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private List<AmenityCluster> mAmenityClusters = new ArrayList<>();
    private AmenityCluster mSelectedAmenityCluster;
    private SelectedMarker mSelectedMarker = new SelectedMarker();
    private NeighborhoodCategoryAdapter mNeighborhoodCategoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private EntityInfo mEntityInfo;
    private Marker mEntityMarker;
    private Integer preSelectDisplayId;
    private String preSelectPlaceID;
    private boolean mIsLocality;

    @Override
    protected int getContentViewId() {
        return R.layout.neighborhood_map_layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCategoryRecyclerView();
        initMap(savedInstanceState);
    }

    @Override
    public void onResume() {
        if(mMapView != null) {
            mMapView.onResume();
        }
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
        if(mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public void setData(EntityInfo entityInfo, List<AmenityCluster> amenityClusters, boolean isLocality){
        mEntityInfo = entityInfo;
        mIsLocality = isLocality;
        if(amenityClusters != null) {
            for (AmenityCluster cluster : amenityClusters) {
                if (null != cluster && null != cluster.cluster && cluster.cluster.size() > 0) {
                    mAmenityClusters.add(cluster);
                }
            }
        }
    }

    public void setDataForPreSelection(Integer displayId,String placeId){
        preSelectDisplayId = displayId;
        preSelectPlaceID = placeId;
    }

    private void initCategoryRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mNeighborhoodCategoryAdapter = new NeighborhoodCategoryAdapter(getActivity(), this);
        mNeighborhoodCategoryView.setLayoutManager(mLayoutManager);
        mNeighborhoodCategoryView.setAdapter(mNeighborhoodCategoryAdapter);
    }


    private void initMap(@Nullable Bundle savedInstanceState) {

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (null == googleMap) {
                    return;
                }

                mPropertyMap = googleMap;
                mPropertyMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (null == mEntityMarker || !marker.getTitle().equalsIgnoreCase(mEntityMarker.getTitle())) {
                            selectMarker(marker);
                        }
                        return false;
                    }
                });

                if (mAmenityClusters != null && mAmenityClusters.size() > 0) {
                    mapAmenityCluster();
                } else {
                    if (mEntityInfo != null) {
                        ((AmenityService) MakaanServiceFactory.getInstance().getService(AmenityService.class))
                                .getAmenitiesByLocation(mEntityInfo.mPlaceLat, mEntityInfo.mPlaceLon, 3,
                                        mIsLocality ? AmenityService.EntityType.LOCALITY : AmenityService.EntityType.PROJECT);
                        showProgress();
                    } else {
                        showNoResults();
                    }
                }

                if (mEntityInfo != null && !TextUtils.isEmpty(mEntityInfo.mPlaceName)) {
                    getActivity().setTitle(mEntityInfo.mPlaceName);
                }

            }
        });


    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if (!isVisible()) {
            return;
        }

        if (amenityGetEvent == null || amenityGetEvent.amenityClusters == null) {
            return;
        }

        mAmenityClusters = amenityGetEvent.amenityClusters;
        mapAmenityCluster();
        showContent();
    }

    private void mapAmenityCluster() {

        if (mAmenityClusters == null && mAmenityClusters.isEmpty()) {
            return;
        }

        mNeighborhoodCategoryAdapter.setData(mAmenityClusters);

        if(preSelectDisplayId!=null) {
            mNeighborhoodCategoryAdapter.setSelectedPosition(preSelectDisplayId);
            populateMarker(preSelectDisplayId);
        } else{
            populateMarker(0);
        }
    }

    private void populateMarker(int selectedCategoryPosition){
        if(mAmenityClusters == null || mAmenityClusters.isEmpty() || mPropertyMap == null){
            return;
        }

        mSelectedAmenityCluster = mAmenityClusters.get(selectedCategoryPosition);
        List<Amenity> amenities = mSelectedAmenityCluster.cluster;
        mAllMarkers.clear();
        clearMap();

        addEntityMarker();

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

    private void addEntityMarker(){
        if(null==mEntityInfo){
            return;
        }
        if(mEntityInfo.mPlaceLat>0 && mEntityInfo.mPlaceLon>0) {
            mEntityMarker = mPropertyMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mEntityInfo.mPlaceLat, mEntityInfo.mPlaceLon))
                    .title(mEntityInfo.mPlaceName));
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
        if(preSelectPlaceID!=null && preSelectPlaceID.equals(amenity.name)){
            selectMarker(marker);
            if(marker!=null){
                marker.showInfoWindow();
            }
        }
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
        }catch(Exception e){
            Crashlytics.logException(e);
        }
    }

    private Bitmap getMarkerBitmap(boolean isSelected, Amenity amenity){
        IconGenerator iconGenerator = new IconGenerator(getActivity());
        iconGenerator.setStyle(isSelected ? IconGenerator.STYLE_RED : IconGenerator.STYLE_WHITE);
        Bitmap markerBitmap = iconGenerator.makeIcon(amenity.displayDistance);
        return markerBitmap;
    }

    @Override
    public void onItemClick(int position, View v) {
        if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity) getActivity()).getScreenName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            properties.put(MakaanEventPayload.LABEL, mAmenityClusters.get(position).name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.mapPropertyLocality);
        }

        if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity) getActivity()).getScreenName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
            properties.put(MakaanEventPayload.LABEL, mAmenityClusters.get(position).name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.mapProjectLocality);
        }

        if (ScreenNameConstants.SCREEN_NAME_LOCALITY.equalsIgnoreCase(((BaseJarvisActivity) getActivity()).getScreenName())) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
            properties.put(MakaanEventPayload.LABEL, mAmenityClusters.get(position).name);
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.mapLocalityNeighbourhood);
        }
        populateMarker(position);
    }

    private class SelectedMarker {
        Marker marker;
        Amenity amenity;
    }

    @OnClick(R.id.previous_amenity)
    public void previousButtonClick(){
        mNeighborhoodCategoryView.getLayoutManager().scrollToPosition(mLayoutManager.findFirstVisibleItemPosition() - 1);
    }

    @OnClick(R.id.next_amenity)
    public void nextButtonClick(){
        mNeighborhoodCategoryView.getLayoutManager().scrollToPosition(mLayoutManager.findLastVisibleItemPosition() + 1);
    }

    public static class EntityInfo{
        public String mPlaceName;
        public double mPlaceLat;
        public double mPlaceLon;

        public EntityInfo(String name, double lat, double lon){
            mPlaceName = name;
            mPlaceLat = lat;
            mPlaceLon = lon;
        }
    }
}
