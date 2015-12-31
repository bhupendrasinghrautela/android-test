package com.makaan.activity.listing;

import android.os.Bundle;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.response.serp.event.ListingGetEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.listing.ListingService;
import com.squareup.otto.Subscribe;

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
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyTypes();
        new ListingService().getListingDetail(1L);
    }

    @Subscribe
    public void onResults(ListingGetEvent listingGetEvent){
        System.out.println("TEST");

    }

}
