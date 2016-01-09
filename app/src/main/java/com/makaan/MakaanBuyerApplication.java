package com.makaan;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.listing.ListingService;
import com.makaan.service.search.SearchService;
import com.makaan.util.RandomString;

/**
 * Created by vaibhav on 23/12/15.
 *
 */
public class MakaanBuyerApplication extends Application {

    public static Selector serpSelector;
    public static RandomString randomString = new RandomString(6);
    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        MakaanNetworkClient.init(this);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

        MakaanServiceFactory.getInstance().registerService(MasterDataService.class, new MasterDataService());
        MakaanServiceFactory.getInstance().registerService(ListingService.class, new ListingService());
        MakaanServiceFactory.getInstance().registerService(SearchService.class , new SearchService());

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroups();


    }

}
