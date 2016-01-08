package com.makaan.activity.serp;

import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.response.serp.event.ListingGetEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
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
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyTypes();
        new ListingService().getListingDetail(1L);
    }

    @Subscribe
    public void onResults(ListingGetEvent listingGetEvent){
        SerpMapFragment fragment = (SerpMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        fragment.setData(listingGetEvent.listingData);
    }
}
