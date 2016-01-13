package com.makaan;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.makaan.cache.LruBitmapCache;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.response.locality.Locality;
import com.makaan.response.serp.FilterGroup;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.city.CityService;
import com.makaan.service.listing.ListingService;
import com.makaan.service.locality.LocalityService;
import com.makaan.service.pyr.PyrService;
import com.makaan.service.search.SearchService;
import com.makaan.util.RandomString;

import java.util.HashMap;

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

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateRentPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsRent();


    }



}
