package com.makaan.activity.listing;

import android.os.Bundle;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.request.selector.Selector;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.listing.ListingService;
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

        Selector selector = new Selector();

        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("2");
        cityList.add("3");
        selector.term("cityId", cityList).term("unitType", "apartment").range("price", 0D, 50000D).page(0, 5);


        System.out.println(selector.build());
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).handleSerpRequest(null);


        /*ArrayList <ApiIntLabel> test = MasterDataCache.getInstance().getAllPropertyTypes();

                ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(323996L);*/
        System.out.println("Test");
    }

    @Subscribe
    public void onResults(SerpGetEvent serpGetEvent){
        System.out.println("TEST");

    }

}
