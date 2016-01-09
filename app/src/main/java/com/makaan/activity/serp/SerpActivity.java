package com.makaan.activity.serp;

import android.os.Bundle;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.service.listing.ListingService;
import com.squareup.otto.Subscribe;

/**
 * Created by sunil on 03/01/16.
 */
public class SerpActivity extends MakaanFragmentActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.serp_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchData();
    }

    private void fetchData(){
        new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
    }

    @Subscribe
    public void onResults(SerpGetEvent serpGetEvent){
        SerpMapFragment fragment = (SerpMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        fragment.setData(serpGetEvent.listingData);
    }
}
