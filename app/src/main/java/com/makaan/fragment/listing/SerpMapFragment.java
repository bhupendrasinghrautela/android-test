package com.makaan.fragment.listing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

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
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.Listing;
import com.makaan.ui.listing.ListingViewPager;
import com.makaan.ui.listing.OnListingPagerChangeListener;
import com.makaan.util.RecentPropertyProjectManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by sunil on 03/01/16.
 */
public class SerpMapFragment extends MakaanBaseFragment {
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private List<Listing> mListings;
    private GoogleMap mPropertyMap;
    private int mTotalCount;
    ListingAndMarkerAdapter adapter = new ListingAndMarkerAdapter();

    @Bind(R.id.serp_map_listing_viewpager)
    ListingViewPager mProjectViewPager;

    @Bind(R.id.serp_map_view)
    MapView mMapView;
    private SerpRequestCallback mCallback;
    private boolean mGooglePlayServicesAvailable = false;

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
        super.onActivityCreated(savedInstanceState);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status == ConnectionResult.SUCCESS) {
            mGooglePlayServicesAvailable = true;
            try {
                initMap(savedInstanceState);
            }catch(Exception e){
                return;
            }
            initPager();
            if (mListings != null) {
                adapter.populateMarker(mListings);

                mProjectViewPager.setData(adapter.listings, mTotalCount > mListings.size(), mCallback);
                adapter.displayProject();
                // display first property be default
//                adapter.setSelectedMarkerPosition(0, true);
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), status).show();
        }

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        mProjectViewPager.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void setData(ArrayList<Listing> listings, int count, SerpRequestCallback callback) {
        if (mListings == null) {
            mListings = new ArrayList<Listing>();
        } else {
            mListings.clear();
        }

        mTotalCount = count;
        mCallback = callback;
        mListings.addAll(listings);

        if (mProjectViewPager != null && mGooglePlayServicesAvailable) {
            adapter.populateMarker(mListings);

            mProjectViewPager.setData(adapter.listings, mTotalCount > mListings.size(), callback);

            mProjectViewPager.setCurrentItem(0);

            adapter.displayProject();
        }
    }

    private void initMap(@Nullable Bundle savedInstanceState) {

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mPropertyMap = mMapView.getMap();

        if (mPropertyMap == null) {
            return;
        }
        mPropertyMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                adapter.displayProject(marker);
                return false;
            }
        });

        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                /*if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    adapter.unselectMarker();
                }*/
                return false;
            }
        });
    }

    private void initPager() {

        mProjectViewPager.bindView();
        mProjectViewPager.addOnPageChangeListener(new OnListingPagerChangeListener() {
            @Override
            public void onPageSelected(int position) {
                /*if (position == mMapListings.size() || mSelectedObject.marker == null || mSelectedObject.listing == null) {
                    return;
                }

                setMarkerIcon(mSelectedObject.marker,
                        getMarkerBitmap(false, mSelectedObject.listing));

                Listing listing = mMapListings.get(position);
                LatLng projectLocation = new LatLng(listing.latitude, listing.longitude);
                mPropertyMap.animateCamera(CameraUpdateFactory.newLatLng(projectLocation));

                Marker marker = mAllMarkers.get(position);
                if (position > 0) {
                    setMarkerIcon(mAllMarkers.get(position - 1),
                            getMarkerBitmap(false, mMapListings.get(position - 1)));
                }

                if ((position + 1) < mAllMarkers.size()) {
                    setMarkerIcon(mAllMarkers.get(position + 1),
                            getMarkerBitmap(false, mMapListings.get(position + 1)));
                }

                setMarkerIcon(marker, getMarkerBitmap(true, mMapListings.get(position)));

                mSelectedObject.marker = marker;
                mSelectedObject.listing = listing;*/
                adapter.setSelectedMarkerPosition(position, false);
            }
        });
    }

    private void clearMap() {
        if (mPropertyMap != null) {
            mPropertyMap.clear();
        }
    }

    private class ListingAndMarkerAdapter {
        ArrayList<Marker> markers = new ArrayList<>();
        ArrayList<Listing> listings = new ArrayList<>();
        ArrayList<ClubbedMarker> clubbedMarkers = new ArrayList<>();
        int selectedMarkerPosition = 0;

        class ClubbedMarker {
            double lat;
            double lng;
            ArrayList<Marker> markers = new ArrayList<>();
        }

        Marker checkIfAlreadyExists(double lat, double lng) {
            for(Marker marker : markers) {
                if(marker.getPosition().latitude == lat && marker.getPosition().longitude == lng) {
                    return marker;
                }
            }
            return null;
        }

        private void populateMarker(List<Listing> listings) {
            this.listings.clear();
            this.listings.addAll(listings);

            markers.clear();
            clubbedMarkers.clear();
            clearMap();

            if (this.listings.isEmpty()) {
                return;
            }

            mLatLngBoundsBuilder = new LatLngBounds.Builder();

            for (int i = 0; i < this.listings.size(); i++) {
                double lat = this.listings.get(i).latitude;
                double lng = this.listings.get(i).longitude;
                if (lat != 0.0 && !Double.isNaN(lat) && lng != 0.0 && !Double.isNaN(lng)) {
                    addMarker(mLatLngBoundsBuilder, lat, lng, this.listings.get(i));
                } else {
                    // remove listing which cannot be represented on the map
                    this.listings.remove(i);
                    i--;
                }
            }
            mapClubbedMarkers();

            animateToLocation(mLatLngBoundsBuilder);
        }

        private void mapClubbedMarkers() {
            for(ClubbedMarker clubbedMarker : clubbedMarkers) {
                int length = clubbedMarker.markers.size();

                for(int i = 0; i < length; i++) {
                    Marker marker = clubbedMarker.markers.get(i);
                    marker.setRotation((360.0f / length) * i);
                    if(i == 0) {
                        marker.setAlpha(1);
                    } else {
                        marker.setAlpha(0.1f);
                    }
                }
            }
        }

        private void handleMarkerAlpha(Marker marker) {
            for(ClubbedMarker clubbedMarker : clubbedMarkers) {
                if(clubbedMarker.lat == marker.getPosition().latitude
                        && clubbedMarker.lng == marker.getPosition().longitude) {
                    int length = clubbedMarker.markers.size();

                    for(int i = 0; i < length; i++) {
                        Marker currentMarker = clubbedMarker.markers.get(i);
                        if(currentMarker == marker) {
                            currentMarker.setAlpha(1);
                        } else {
                            currentMarker.setAlpha(0.1f);
                        }
                    }
                }
            }
        }

        private void addMarker(LatLngBounds.Builder latLngBoundsBuilder,
                               double lat, double lng, Listing listing) {
            ClubbedMarker clubbedMarker = null;
            Marker marker = checkIfAlreadyExists(lat, lng);
            if(marker != null) {
                for(ClubbedMarker club : clubbedMarkers) {
                    if(club.lat == lat && club.lng == lng) {
                        clubbedMarker = club;
                        break;
                    }
                }
                if(clubbedMarker == null) {
                    clubbedMarker = new ClubbedMarker();
                    clubbedMarker.lat = lat;
                    clubbedMarker.lng = lng;
                    clubbedMarker.markers.add(marker);
                }
                clubbedMarkers.add(clubbedMarker);
            }

            /*IconGenerator iconGenerator = new IconGenerator(getActivity());
            iconGenerator.setStyle(IconGenerator.STYLE_RED);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconGenerator.setBackground(getContext().getResources().getDrawable(R.drawable.map_1, null));
            } else {
                iconGenerator.setBackground(getContext().getResources().getDrawable(R.drawable.map_1));
            }
            Bitmap markerBitmap =
                    iconGenerator.makeIcon(String.valueOf(listing.bedrooms));*/
            Bitmap mapIcon = generateMapPointer(false, listing);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(mapIcon));

            Marker newMarker = mPropertyMap.addMarker(markerOptions);
            Log.d("DEBUG", "lat = " + lat + ", lng = " + lng);

            if(clubbedMarker != null) {
                clubbedMarker.markers.add(newMarker);
            }
            latLngBoundsBuilder.include(new LatLng(lat, lng));
            markers.add(newMarker);
        }

        private Bitmap generateMapPointer(boolean isSelected, Listing listing) {
            ViewGroup container = (ViewGroup)LayoutInflater.from(getContext()).inflate(R.layout.map_pointer, (ViewGroup)null);
            if(listing.id != null) {
                if (RecentPropertyProjectManager.getInstance(getActivity()).containsProperty(listing.id)) {
                    container.setBackgroundResource(R.drawable.map_2);
                } else {
                    container.setBackgroundResource(R.drawable.map_1);
                }
            }
            float multiplier = 0.9f;
            if(isSelected) {
                multiplier = 1.0f;
            }
            ((TextView)container.findViewById(R.id.map_pointer_text)).setText(String.valueOf(listing.bedrooms));
            int measureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            container.measure(measureSpec, measureSpec);
            int measuredWidth = (int) (container.getMeasuredWidth() * multiplier);
            int measuredHeight = (int) (container.getMeasuredHeight() * multiplier);
            container.layout(0, 0, measuredWidth, measuredHeight);

            Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            r.eraseColor(0);
            Canvas canvas = new Canvas(r);

            container.draw(canvas);
            return r;
        }

        private void animateToLocation(final LatLngBounds.Builder latLngBoundsBuilder) {

            if (mMapView == null) {
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
                                    } catch (Exception e) {
                                    }
                                }
                            });
                }
            }
        }

        private void displayProject(Marker marker) {
            if (markers.contains(marker)) {
                int pos = markers.indexOf(marker);
                mProjectViewPager.setCurrentItem(pos, true);
                selectMarker(pos);
            }
        }

        private void displayProject(int pos) {
            if (pos < markers.size()) {
                mProjectViewPager.setCurrentItem(pos, true);
                selectMarker(pos);
            }
        }

        void setSelectedMarkerPosition(int position, boolean displayProperty) {
            unselectMarker(selectedMarkerPosition);

            if (position < markers.size()) {
                mProjectViewPager.setVisibility(View.VISIBLE);
                selectedMarkerPosition = position;
                if (displayProperty) {
                    displayProject(selectedMarkerPosition);
                }

                setMarkerIcon(markers.get(selectedMarkerPosition),
                        getMarkerBitmap(true, listings.get(selectedMarkerPosition)));

                LatLng projectLocation = new LatLng(listings.get(selectedMarkerPosition).latitude, listings.get(selectedMarkerPosition).longitude);
                mPropertyMap.animateCamera(CameraUpdateFactory.newLatLng(projectLocation));

                handleMarkerAlpha(markers.get(selectedMarkerPosition));
            }

        }

        public void displayProject() {
            setSelectedMarkerPosition(selectedMarkerPosition, true);
        }

        private void setMarkerIcon(Marker marker, Bitmap icon) {
            try {
                if (marker != null) {
                    marker.hideInfoWindow();
                    marker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(icon));
                }
            } catch (Exception e) {
            }
        }

        private Bitmap getMarkerBitmap(boolean isSelected, Listing listing) {
            /*IconGenerator iconGenerator = new IconGenerator(getActivity());
            iconGenerator.setStyle(isSelected ? IconGenerator.STYLE_ORANGE : IconGenerator.STYLE_RED);
            Bitmap markerBitmap = iconGenerator.makeIcon(String.valueOf(listing.bedrooms));*/
            return generateMapPointer(isSelected, listing);
        }


        private boolean unselectMarker(int i) {
            if(i < listings.size()) {
                Bitmap markerBitmap = getMarkerBitmap(false, listings.get(i));
                setMarkerIcon(markers.get(i), markerBitmap);
                return true;
            }
            return false;
        }

        private boolean selectMarker(int i) {
            if(i < listings.size()) {
                Bitmap markerBitmap = getMarkerBitmap(true, listings.get(i));
                setMarkerIcon(markers.get(i), markerBitmap);
                return true;
            }
            return false;
        }
    }



}
