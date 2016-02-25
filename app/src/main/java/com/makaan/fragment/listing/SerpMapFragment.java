package com.makaan.fragment.listing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.Listing;
import com.makaan.ui.listing.ListingViewPager;
import com.makaan.ui.listing.OnListingPagerChangeListener;
import com.makaan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * Created by sunil on 03/01/16.
 */
public class SerpMapFragment extends MakaanBaseFragment implements ClusterManager.OnClusterClickListener<SerpMapFragment.PropertyItem>,
        ClusterManager.OnClusterInfoWindowClickListener<SerpMapFragment.PropertyItem>, ClusterManager.OnClusterItemClickListener<SerpMapFragment.PropertyItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<SerpMapFragment.PropertyItem> {
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private List<Listing> mListings;
    private GoogleMap mPropertyMap;
    private int mTotalCount;
    private ClusterManager<PropertyItem> mClusterManager;

    @Override
    public boolean onClusterClick(Cluster<PropertyItem> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PropertyItem> cluster) {

    }

    @Override
    public boolean onClusterItemClick(PropertyItem propertyItem) {
        mProjectViewPager.setCurrentItem(propertyItem.position);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PropertyItem propertyItem) {

    }

    public class PropertyItem implements ClusterItem {
        private final int position;
        public LatLng geoPosition;
        public String displayText;

        public PropertyItem(double lat, double lng, String displayText, int i) {
            geoPosition = new LatLng(lat, lng);
            this.displayText = displayText;
            this.position = i;
        }

        @Override
        public LatLng getPosition() {
            return geoPosition;
        }
    }

    class PropertyItemRenderer extends DefaultClusterRenderer<PropertyItem> {
        private final IconGenerator mIconGenerator;
        private final IconGenerator mClusterIconGenerator;
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimensionWidth;
        private final int mDimensionHeight;

        public PropertyItemRenderer(Context context, GoogleMap map, ClusterManager<PropertyItem> clusterManager) {
            super(context, map, clusterManager);
            mIconGenerator = new IconGenerator(context);
            mClusterIconGenerator = new IconGenerator(context);
            int padding = 0;

            mClusterImageView = new ImageView(context);
            mDimensionWidth = (int) getResources().getDimension(R.dimen.map_pointer_width);
            mDimensionHeight = (int) getResources().getDimension(R.dimen.map_pointer_height);
            mClusterImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimensionWidth, mDimensionHeight));
            mClusterImageView.setPadding(padding, padding, padding, padding);
            mClusterIconGenerator.setContentView(mClusterImageView);

            mImageView = new ImageView(context);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimensionWidth, mDimensionHeight));
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(PropertyItem item, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            Bitmap icon = generateMapPointer(false, String.valueOf(item.displayText), true);
            mImageView.setImageBitmap(icon);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<PropertyItem> cluster, MarkerOptions markerOptions) {
            Bitmap icon = generateMapPointer(false, String.valueOf(cluster.getSize()), true);
            mClusterImageView.setImageBitmap(icon);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

        private Bitmap generateMapPointer(boolean isSelected, String text, boolean isCluster) {
            ViewGroup container = (ViewGroup)LayoutInflater.from(getContext()).inflate(R.layout.map_pointer, (ViewGroup)null);
            if(isCluster) {
                container.setBackgroundResource(R.drawable.map_cluster);
            } else {
                if (isSelected) {
                    container.setBackgroundResource(R.drawable.map_single_selected);
                } else {
                    container.setBackgroundResource(R.drawable.map_single_normal);
                }
            }

            ((TextView)container.findViewById(R.id.map_pointer_text)).setText(String.valueOf(text));
            int measureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            container.measure(measureSpec, measureSpec);
            int measuredWidth = mDimensionWidth;
            int measuredHeight = mDimensionHeight;
            container.layout(0, 0, measuredWidth, measuredHeight);

            Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            r.eraseColor(0);
            Canvas canvas = new Canvas(r);

            container.draw(canvas);
            return r;
        }
    }

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
                populatePropertyItems();

                mProjectViewPager.setData(mListings, mTotalCount > mListings.size(), mCallback);
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
            mClusterManager.clearItems();

            populatePropertyItems();

            mProjectViewPager.setData(mListings, mTotalCount > mListings.size(), callback);

            mProjectViewPager.setCurrentItem(0);
        }
    }

    private void populatePropertyItems() {
        if(mListings != null) {
            for (int i = 0; i < this.mListings.size(); i++) {
                double lat = this.mListings.get(i).latitude;
                double lng = this.mListings.get(i).longitude;
                if (lat != 0.0 && !Double.isNaN(lat) && lng != 0.0 && !Double.isNaN(lng)) {
                    if(mLatLngBoundsBuilder == null) {
                        mLatLngBoundsBuilder = new LatLngBounds.Builder();
                    }
                    mLatLngBoundsBuilder.include(new LatLng(lat, lng));
                    mClusterManager.addItem(new PropertyItem(lat, lng, StringUtil.getDisplayPrice(mListings.get(i).price), i));
                } else {
                    // remove listing which cannot be represented on the map
                    mListings.remove(i);
                    i--;
                }
            }
            animateToLocation(mLatLngBoundsBuilder);
        }
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

    private void initMap(@Nullable Bundle savedInstanceState) {

        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mPropertyMap = mMapView.getMap();

        if (mPropertyMap == null) {
            return;
        }


        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<PropertyItem>(getActivity(), mPropertyMap);
        mClusterManager.setRenderer(new PropertyItemRenderer(getActivity(), mPropertyMap, mClusterManager));
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mPropertyMap.setOnCameraChangeListener(mClusterManager);
        mPropertyMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);


    }

    private void initPager() {

        mProjectViewPager.bindView();
        mProjectViewPager.addOnPageChangeListener(new OnListingPagerChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                adapter.setSelectedMarkerPosition(position, false);
            }
        });
    }

    private void clearMap() {
        if (mPropertyMap != null) {
            mPropertyMap.clear();
        }
    }



}
