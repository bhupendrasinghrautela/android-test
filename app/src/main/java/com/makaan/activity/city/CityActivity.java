package com.makaan.activity.city;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.event.PageTag;
import com.makaan.response.listing.Listing;
import com.makaan.service.CityService;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

/**
 * Created by aishwarya on 19/01/16.
 */
public class CityActivity extends MakaanFragmentActivity {
    public static final String CITY_ID = "city_id";

    private CityOverViewFragment mCityOverViewFragment;
    private long mCityId;
    private String cityName;

    @Override
    protected int getContentViewId() {
        return R.layout.city_activity_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null) {

            //mCityId = intent.getLongExtra(CITY_ID, 5l);
            mCityId = intent.getLongExtra(CITY_ID, 1l);
        }
        mCityOverViewFragment = new CityOverViewFragment();
        initFragment(R.id.container, mCityOverViewFragment, false);
        fetchData();
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return "City";
    }

    private void fetchData() {
        new CityService().getCityById(mCityId);
        new CityService().getTopLocalitiesInCity(mCityId, 4);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickLocality);
                finish();
                break;
        }
        return true;
    }

    @Subscribe
    public void onResults(CityByIdEvent cityByIdEvent){
        if (null != cityByIdEvent && null!=cityByIdEvent.city) {

            PageTag pageTag = new PageTag();
            pageTag.addCity(cityByIdEvent.city.label);
            super.setCurrentPageTag(pageTag);

            cityName = cityByIdEvent.city.label;
        }
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        if(null==event.message){
            return;
        }
        if(!TextUtils.isEmpty(cityName)) {
            event.message.city = cityName;
            displayPopupWindow(event.message);
        }
    }

    @Override
    public void onBackPressed() {
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backToHome);
        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickLocality);
        super.onBackPressed();
    }
}
