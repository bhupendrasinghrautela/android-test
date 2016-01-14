package com.makaan;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.CityService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.PyrService;
import com.makaan.service.SearchService;
import com.makaan.service.TaxonomyService;
import com.makaan.util.RandomString;

/**
 * Created by vaibhav on 23/12/15.
 *
 */
public class MakaanBuyerApplication extends Application {
    public static final String TAG = MakaanBuyerApplication.class.getSimpleName();


    public static RandomString randomString = new RandomString(6);
    public static Selector serpSelector  = new Selector();
    public static boolean isBuySearch = true;

    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();

        MakaanNetworkClient.init(this);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

        MakaanServiceFactory.getInstance().registerService(MasterDataService.class, new MasterDataService());
        MakaanServiceFactory.getInstance().registerService(ListingService.class, new ListingService());
        MakaanServiceFactory.getInstance().registerService(SearchService.class , new SearchService());
        MakaanServiceFactory.getInstance().registerService(CityService.class , new CityService());
        MakaanServiceFactory.getInstance().registerService(PyrService.class , new PyrService());
        MakaanServiceFactory.getInstance().registerService(LocalityService.class , new LocalityService());
        MakaanServiceFactory.getInstance().registerService(PriceTrendService.class , new PriceTrendService());
        MakaanServiceFactory.getInstance().registerService(TaxonomyService.class , new TaxonomyService());

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateRentPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsRent();


    }



}
