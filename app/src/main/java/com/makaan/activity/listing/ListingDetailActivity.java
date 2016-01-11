package com.makaan.activity.listing;

import android.os.Bundle;
import android.view.View;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.request.selector.Selector;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.city.CityService;
import com.makaan.service.listing.ListingService;
import com.makaan.service.locality.LocalityService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingDetailActivity extends MakaanFragmentActivity {


    @Override
    protected int getContentViewId() {
        return R.layout.content_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @OnClick(R.id.fetch)
    public void fetch(View view) {

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateRentPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsRent();


        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).handleSerpRequest(MakaanBuyerApplication.serpSelector);
        ((CityService) (MakaanServiceFactory.getInstance().getService(CityService.class))).getCityById(2L);
        ((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getLocalityById(50186L);
    }

    @Subscribe
    public void onResults(SerpGetEvent serpGetEvent) {
        System.out.println("TEST");
    }

}
