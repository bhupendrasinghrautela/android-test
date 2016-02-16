package com.makaan.activity.city;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.service.CityService;
import com.squareup.otto.Subscribe;

/**
 * Created by aishwarya on 19/01/16.
 */
public class CityActivity extends MakaanFragmentActivity {
    public static final String CITY_ID = "city_id";

    private CityOverViewFragment mCityOverViewFragment;
    private long mCityId;

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

    private void fetchData() {
        new CityService().getCityById(mCityId);
        new CityService().getTopLocalitiesInCity(mCityId, 4);
        new CityService().getPropertyRangeInCity(mCityId,null,null,false,10000,500000,50000);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }
}
