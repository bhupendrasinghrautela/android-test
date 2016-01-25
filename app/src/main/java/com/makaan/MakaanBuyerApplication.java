package com.makaan;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makaan.cookie.MakaanCookieStore;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.jarvis.JarvisServiceCreator;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.service.AgentService;
import com.makaan.service.AmenityService;
import com.makaan.service.BlogService;
import com.makaan.service.BuilderService;
import com.makaan.service.CityService;
import com.makaan.service.ImageService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.OtpVerificationService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.ProjectService;
import com.makaan.service.PyrService;
import com.makaan.service.SearchService;
import com.makaan.service.TaxonomyService;
import com.makaan.service.UserService;
import com.makaan.service.WishListService;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.RandomString;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

/**
 * Created by vaibhav on 23/12/15.
 */
public class MakaanBuyerApplication extends Application {
    public static final String TAG = MakaanBuyerApplication.class.getSimpleName();


    public static RandomString randomString = new RandomString(6);
    public static Selector serpSelector = new Selector();

    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();

        MakaanNetworkClient.init(this);
        JarvisServiceCreator.create(this);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

        MakaanServiceFactory.getInstance().registerService(MasterDataService.class, new MasterDataService());
        MakaanServiceFactory.getInstance().registerService(ListingService.class, new ListingService());
        MakaanServiceFactory.getInstance().registerService(SearchService.class, new SearchService());
        MakaanServiceFactory.getInstance().registerService(CityService.class, new CityService());
        MakaanServiceFactory.getInstance().registerService(PyrService.class, new PyrService());

        MakaanServiceFactory.getInstance().registerService(LocalityService.class, new LocalityService());
        MakaanServiceFactory.getInstance().registerService(PriceTrendService.class, new PriceTrendService());
        MakaanServiceFactory.getInstance().registerService(TaxonomyService.class, new TaxonomyService());
        MakaanServiceFactory.getInstance().registerService(UserService.class, new UserService());
        MakaanServiceFactory.getInstance().registerService(ImageService.class, new ImageService());
        //MakaanServiceFactory.getInstance().registerService(TopAgentsByLocalityService.class , new TopAgentsByLocalityService());
        MakaanServiceFactory.getInstance().registerService(BuilderService.class, new BuilderService());
        MakaanServiceFactory.getInstance().registerService(ProjectService.class, new ProjectService());
        MakaanServiceFactory.getInstance().registerService(BlogService.class, new BlogService());
        MakaanServiceFactory.getInstance().registerService(UserLoginService.class, new UserLoginService());
        MakaanServiceFactory.getInstance().registerService(WishListService.class, new WishListService());
        MakaanServiceFactory.getInstance().registerService(AgentService.class, new AgentService());
        MakaanServiceFactory.getInstance().registerService(OtpVerificationService.class, new OtpVerificationService());
        MakaanServiceFactory.getInstance().registerService(AmenityService.class, new AmenityService());

        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateRentPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsRent();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePyrGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePyrGroupsRent();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateAmenityMap();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateSearchType();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyAmenities();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateMasterFunishings();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyDisplayOrder();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBhkList();

        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisMessageType();

        CookieStore cookieStore = new MakaanCookieStore(getApplicationContext());
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

    }


}
