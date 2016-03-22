package com.makaan.activity.overview;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.city.CityOverViewFragment;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.fragment.locality.LocalityFragment;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.event.PageTag;
import com.makaan.pojo.overview.OverviewBackStack;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.project.Project;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.AmenityService;
import com.makaan.service.MakaanServiceFactory;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

/**
 * Created by rohitgarg on 3/21/16.
 */
public class OverviewActivity extends MakaanBaseSearchActivity implements OverviewFragment.OverviewActivityCallbacks {
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PLACE_NAME = "place_name";

    public static final String SCREEN_NAME_LISTING_DETAIL = "listing detail";
    public static final String SCREEN_NAME_PROJECT = "project";
    public static final String SCREEN_NAME_LOCALITY = "locality";
    public static final String SCREEN_NAME_CITY = "city";
    OverviewBackStack mOverviewBackStack = new OverviewBackStack();

    Long id;
    String mName;
    private OverviewItemType mType;
    private Long mId;
    private OverviewFragment mFragment;
    private int mTotalImagesSeen;
    private NeighborhoodMapFragment.EntityInfo mEntityInfo;
    private AmenityGetEvent mAmenityGetEvent;

    @Override
    protected int getContentViewId() {
        return R.layout.city_activity_layout;
    }

    @Override
    protected boolean needScrollableSearchBar() {
        return false;
    }

    @Override
    protected boolean supportsListing() {
        return false;
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        if(mType != null) {
            switch (mType) {
                case PROPERTY:
                case PROPERTY_MAP:
                    return SCREEN_NAME_LISTING_DETAIL;
                case PROJECT:
                case PROJECT_MAP:
                    return SCREEN_NAME_PROJECT;
                case LOCALITY:
                case LOCALITY_MAP:
                    return SCREEN_NAME_LOCALITY;
                case CITY:
                    return SCREEN_NAME_CITY;
            }
        } else if(getIntent() != null && getIntent().getExtras() != null) {
            int type = getIntent().getExtras().getInt(TYPE, -1);
            if (type != -1) {
                switch (OverviewItemType.values()[type]) {
                    case PROPERTY:
                    case PROPERTY_MAP:
                        return SCREEN_NAME_LISTING_DETAIL;
                    case PROJECT:
                    case PROJECT_MAP:
                        return SCREEN_NAME_PROJECT;
                    case LOCALITY:
                    case LOCALITY_MAP:
                        return SCREEN_NAME_LOCALITY;
                    case CITY:
                        return SCREEN_NAME_CITY;
                }
            }
        }
        return "Overview Activity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleLaunchIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleLaunchIntent(intent);
    }

    void handleLaunchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();

        if (extras.containsKey(TYPE) && extras.containsKey(ID)) {

            // todo handle diff type cases
            if (mTotalImagesSeen > 0) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, mTotalImagesSeen);
                MakaanEventPayload.endBatch(getApplicationContext(), MakaanTrackerConstants.Action.clickPropertyImages);
            }
            if(mType != null) {
                if(mType == OverviewItemType.CITY || mType == OverviewItemType.LOCALITY
                        || mType == OverviewItemType.PROJECT || mType == OverviewItemType.PROPERTY) {
                    if (mId != null) {
                        mOverviewBackStack.addToBackStack(mType, mId);
                    }
                } else if(mType == OverviewItemType.LOCALITY_MAP || mType == OverviewItemType.PROJECT_MAP
                        || mType == OverviewItemType.PROPERTY_MAP) {
                    if(mId != null && mEntityInfo != null) {
                        mOverviewBackStack.addToBackStack(mType, mId, mEntityInfo.mPlaceLat, mEntityInfo.mPlaceLon, mEntityInfo.mPlaceName);
                    }
                }
            }

            int type = extras.getInt(TYPE, -1);
            if (type == -1) {
                return;
            }
            clearCurrentState(false);

            mType = OverviewItemType.values()[type];
            mId = extras.getLong(ID, 0);

            if(mType == OverviewItemType.LOCALITY_MAP || mType == OverviewItemType.PROJECT_MAP
                    || mType == OverviewItemType.PROPERTY_MAP) {
                String name = extras.getString(PLACE_NAME);
                Double lat = extras.getDouble(LATITUDE);
                Double lon = extras.getDouble(LONGITUDE);
                if(name != null && !Double.isNaN(lat) && !Double.isNaN(lon)) {
                    mEntityInfo = new NeighborhoodMapFragment.EntityInfo(name, lat, lon);
                }
            }
            switch (mType) {
                case PROPERTY:
                    mFragment = new PropertyDetailFragment();
                    mFragment.setArguments(extras);
                    mFragment.bindView(this);
                    initFragment(R.id.container, mFragment, false);
                    initUi(true);
                    break;
                case PROJECT:
                    mFragment = new ProjectFragment();
                    mFragment.setArguments(extras);
                    mFragment.bindView(this);
                    initFragment(R.id.container, mFragment, false);
                    initUi(true);
                    break;
                case LOCALITY:
                    mFragment = new LocalityFragment();
                    mFragment.setArguments(extras);
                    mFragment.bindView(this);
                    initFragment(R.id.container, mFragment,false);
                    initUi(false);
                    break;
                case CITY:
                    mFragment = new CityOverViewFragment();
                    mFragment.setArguments(extras);
                    initFragment(R.id.container, mFragment, false);
                    initUi(false);
                    break;
                case PROPERTY_MAP:
                    if (mAmenityGetEvent != null) {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, mAmenityGetEvent.amenityClusters, false);
                        initFragment(R.id.container, fragment, false);
                    } else {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, null, false);
                        initFragment(R.id.container, fragment, false);
                    }
                    initUi(true);
                    break;
                case PROJECT_MAP:
                    if (mAmenityGetEvent != null) {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, mAmenityGetEvent.amenityClusters, false);
                        initFragment(R.id.container, fragment, false);
                    } else {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, null, false);
                        initFragment(R.id.container, fragment, false);
                    }
                    initUi(true);
                    break;
                case LOCALITY_MAP:
                    if (mAmenityGetEvent != null) {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, mAmenityGetEvent.amenityClusters, true);
                        initFragment(R.id.container, fragment, false);
                    } else {
                        NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
                        fragment.setData(mEntityInfo, null, true);
                        initFragment(R.id.container, fragment, false);
                    }
                    initUi(true);
                    break;
            }
        }
    }

    void clearCurrentState(boolean clearAmenityData) {
        id = null;
        mName = null;
        mType = null;
        mTotalImagesSeen = 0;
        if(clearAmenityData) {
            mEntityInfo = null;
            mAmenityGetEvent = null;
        }
    }

    @Override
    public void showMapFragment() {
        Intent intent = new Intent(this, OverviewActivity.class);
        Bundle extras = new Bundle();
        extras.putLong(ID, mId);
        if(mType == OverviewItemType.LOCALITY) {
            extras.putInt(TYPE, OverviewItemType.LOCALITY_MAP.ordinal());
        } else if(mType == OverviewItemType.PROJECT) {
            extras.putInt(TYPE, OverviewItemType.PROJECT_MAP.ordinal());
        } else if(mType == OverviewItemType.PROPERTY) {
            extras.putInt(TYPE, OverviewItemType.PROPERTY_MAP.ordinal());
        }
        intent.putExtras(extras);
        startActivity(intent);
        /*if(mAmenityGetEvent!=null) {
            mNeighborhoodMapFragment = new NeighborhoodMapFragment();
            mNeighborhoodMapFragment.setData(mEntityInfo, mAmenityGetEvent.amenityClusters);
            initFragment(R.id.container, mNeighborhoodMapFragment, true);
        }*/
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if (isActivityDead()) {
            return;
        }

        if (amenityGetEvent.amenityClusters == null) {
            return;
        }
        mAmenityGetEvent = amenityGetEvent;
        /*if(mType == OverviewItemType.LOCALITY_MAP || mType == OverviewItemType.PROJECT_MAP
                || mType == OverviewItemType.PROPERTY_MAP) {
            NeighborhoodMapFragment fragment = new NeighborhoodMapFragment();
            fragment.setData(mEntityInfo, mAmenityGetEvent.amenityClusters);
            initFragment(R.id.container, fragment, false);
            showContent();
        }*/
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event) {
        if (isActivityDead()) {
            return;
        }

        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        if(isActivityDead()){
            return;
        }
        if(null==event.message){
            return;
        }
        if(!TextUtils.isEmpty(mName)) {
            event.message.city = mName;
            displayPopupWindow(event.message);
        }
    }

    @Override
    public void imagesSeenCount(int count) {
        mTotalImagesSeen = count;
    }

    @Override
    public void onBackPressed() {
        if (needBackProcessing()) {
            super.onBackPressed();
        } else {
            // todo handle diff type cases
            if (mTotalImagesSeen > 0) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, mTotalImagesSeen);
                MakaanEventPayload.endBatch(getApplicationContext(), MakaanTrackerConstants.Action.clickPropertyImages);
            }
            OverviewBackStack.SingleOverviewItem item = mOverviewBackStack.pop();
            if(item != null) {
                clearCurrentState(true);
                Intent intent = new Intent(this, OverviewActivity.class);
                Bundle extras = new Bundle();
                extras.putLong(ID, item.id);
                extras.putInt(TYPE, item.type.ordinal());
                if(item.placeName != null) {
                    extras.putString(PLACE_NAME, item.placeName);
                }
                if(item.latitude != null) {
                    extras.putDouble(LATITUDE, item.latitude);
                }
                if(item.longitude != null) {
                    extras.putDouble(LONGITUDE, item.longitude);
                }

                intent.putExtras(extras);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        if (isActivityDead()) {
            return;
        }

        if (listingByIdGetEvent != null && listingByIdGetEvent.listingDetail != null
                && null == listingByIdGetEvent.error) {
            ListingDetail listingDetail = listingByIdGetEvent.listingDetail;
            mEntityInfo = new NeighborhoodMapFragment.EntityInfo(listingDetail.property.project.builder.name + " " + listingDetail.property.project.name,
                    listingDetail.latitude, listingDetail.longitude);
        }
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent) {
        if (isActivityDead()) {
            return;
        }

        if (null != projectByIdEvent && null == projectByIdEvent.error) {
            Project project = projectByIdEvent.project;
            if (null != project && project.latitude != null && project.longitude != null) {
                if(project.builder != null && !TextUtils.isEmpty(project.builder.name)) {
                    mName = String.format("%s %s", project.builder.name, project.name);
                } else {
                    mName = project.name;

                }
                mEntityInfo = new NeighborhoodMapFragment.EntityInfo(mName, project.latitude, project.longitude);
                PageTag pageTag = new PageTag();
                pageTag.addCity(project.name);
                super.setCurrentPageTag(pageTag);
            }
        }
    }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent) {
        if(isActivityDead()){
            return;
        }

        if (null != localityByIdEvent && null != localityByIdEvent.locality) {

            PageTag pageTag = new PageTag();
            pageTag.addLocality(localityByIdEvent.locality.label);
            super.setCurrentPageTag(pageTag);

            if(localityByIdEvent.locality.latitude != null && localityByIdEvent.locality.longitude != null) {
                mEntityInfo = new NeighborhoodMapFragment.EntityInfo(localityByIdEvent.locality.label,
                        localityByIdEvent.locality.latitude,
                        localityByIdEvent.locality.longitude);
            }

            mName = localityByIdEvent.locality.label;
        }
    }

    @Subscribe
    public void onResults(CityByIdEvent cityByIdEvent){
        if(isActivityDead()){
            return;
        }
        if (null != cityByIdEvent && null!=cityByIdEvent.city) {

            PageTag pageTag = new PageTag();
            pageTag.addCity(cityByIdEvent.city.label);
            super.setCurrentPageTag(pageTag);

            mName = cityByIdEvent.city.label;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
