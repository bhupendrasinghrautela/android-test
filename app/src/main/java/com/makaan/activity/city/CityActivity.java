package com.makaan.activity.city;

import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.service.CityService;

/**
 * Created by aishwarya on 19/01/16.
 */
public class CityActivity extends MakaanFragmentActivity {
    private CityOverViewFragment mCityOverViewFragment;
    @Override
    protected int getContentViewId() {
        return R.layout.city_activity_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCityOverViewFragment = new CityOverViewFragment();
        initFragment(R.id.container, mCityOverViewFragment, true);
        fetchData();
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    private void fetchData() {
        new CityService().getCityById(5l);
        new CityService().getTopLocalitiesInCity(5l, 4);
    }
}
