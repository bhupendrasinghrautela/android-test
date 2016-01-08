package com.makaan;

import android.app.Application;

import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.SerpRequest;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.listing.ListingService;
import com.makaan.service.search.SearchService;

/**
 * Created by vaibhav on 23/12/15.
 *
 */
public class MakaanBuyerApplication extends Application {

    public static SerpRequest serpRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        MakaanNetworkClient.init(this);


        MakaanServiceFactory.getInstance().registerService(MasterDataService.class, new MasterDataService());
        MakaanServiceFactory.getInstance().registerService(ListingService.class , new ListingService());
        MakaanServiceFactory.getInstance().registerService(SearchService.class , new SearchService());

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroups();


    }

}
