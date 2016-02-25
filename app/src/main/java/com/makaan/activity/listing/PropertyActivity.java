package com.makaan.activity.listing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.adapter.property.PropertyImagesPagerAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

/**
 * Created by sunil on 17/01/16.
 */
public class PropertyActivity extends MakaanBaseSearchActivity implements ShowMapCallBack ,TotalImagesCount{

    public static final String LISTING_ID = "listingId";

    private PropertyDetailFragment mPropertyDeatilFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;
    private AmenityGetEvent mAmenityGetEvent;
    private long mListingId;
    private double mListingLon;
    private double mListingLat;
    private NeighborhoodMapFragment.EntityInfo mEntityInfo;
    private int mTotalImagesSeen=0;

    @Override
    protected int getContentViewId() {
        return R.layout.property_activity_layout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            mListingId = extras.getLong(KeyUtil.LISTING_ID);
            mListingLon = extras.getDouble(KeyUtil.LISTING_LON);
            mListingLat = extras.getDouble(KeyUtil.LISTING_LAT);
            mPropertyDeatilFragment = new PropertyDetailFragment();
            mPropertyDeatilFragment.setArguments(extras);
            mPropertyDeatilFragment.bindView(this);
            initFragment(R.id.container, mPropertyDeatilFragment, false);
        }

        fetchProjectDetail();
        initUi(true);

    }

    private void fetchProjectDetail(){
        //Intent intent = getIntent();
        //long listingId = intent.getExtras().getLong("listingId");
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(mListingId);
        //TODO correct similar listing
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getSimilarListingDetail(mListingId);
    }



    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return "Listing detail";
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }

    @Override
    protected boolean needScrollableSearchBar() {
        return false;
    }

    @Override
    protected boolean supportsListing() {
        return false;
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(amenityGetEvent.amenityClusters == null){
            return;
        }
        mAmenityGetEvent = amenityGetEvent;
    }

    @Produce
    public AmenityGetEvent produceAmenityEvent(){
        return mAmenityGetEvent;
    }

    @Override
    public void showMapFragment() {
        mNeighborhoodMapFragment = new NeighborhoodMapFragment();
        mNeighborhoodMapFragment.setData(mEntityInfo,mAmenityGetEvent.amenityClusters);
        initFragment(R.id.container, mNeighborhoodMapFragment, true);
        //produceAmenityEvent();
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        if(listingByIdGetEvent.listingDetail ==null || null != listingByIdGetEvent.error){
            return;
        }else {
            ListingDetail listingDetail = listingByIdGetEvent.listingDetail;
            if(null!=listingDetail) {
                mEntityInfo = new NeighborhoodMapFragment.EntityInfo(listingDetail.property.project.builder.name + " " + listingDetail.property.project.name ,
                        listingDetail.latitude, listingDetail.longitude);
            }
        }
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Override
    public void ImageCount(int count) {
        mTotalImagesSeen=count;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
        properties.put(MakaanEventPayload.LABEL, mTotalImagesSeen);
        MakaanEventPayload.endBatch(getApplicationContext(), MakaanTrackerConstants.Action.clickPropertyImages);
    }
}
