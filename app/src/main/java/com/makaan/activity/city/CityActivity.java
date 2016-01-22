package com.makaan.activity.city;

import android.content.Intent;
import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.service.CityService;

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
            mCityId = 2L;
        }
        mCityOverViewFragment = new CityOverViewFragment();
        initFragment(R.id.container, mCityOverViewFragment, true);
        fetchData();
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    private void fetchData() {
        new CityService().getCityById(mCityId);
        new CityService().getTopLocalitiesInCity(mCityId, 4);
    }
}
