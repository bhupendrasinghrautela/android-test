package com.makaan;

import android.app.Application;
import android.content.Context;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.cookie.MakaanCookieStore;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.jarvis.JarvisServiceCreator;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.service.LocationService;
import com.makaan.service.leakcanary.TemporaryLeakUploadService;
import com.makaan.service.user.ForgotPasswordService;
import com.makaan.service.user.UserRegistrationService;
import com.makaan.service.AgentService;
import com.makaan.service.AmenityService;
import com.makaan.service.BlogService;
import com.makaan.service.BuilderService;
import com.makaan.service.CityService;
import com.makaan.service.ImageService;
import com.makaan.service.LeadInstantCallbackService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.OtpVerificationService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.ProjectService;
import com.makaan.service.PyrService;
import com.makaan.service.SaveSearchService;
import com.makaan.service.SearchService;
import com.makaan.service.SellerService;
import com.makaan.service.TaxonomyService;
import com.makaan.service.UserService;
import com.makaan.service.WishListService;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.FontTypeface;
import com.makaan.util.RandomString;
import com.segment.analytics.Analytics;
import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
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
//    public static Selector mSerpSelector = new Selector();
    public static SparseArray<SerpObjects> serpObjects = new SparseArray<>();

    public static Gson gson;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MakaanBuyerApplication application = (MakaanBuyerApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // leak canary to detect memory leaks
        // comment below line to disable leak canary
        refWatcher = LeakCanary.install(this, TemporaryLeakUploadService.class, AndroidExcludedRefs.createAppDefaults().build());

        FontTypeface.setDefaultFont(this, "DEFAULT", "fonts/ProximaNova-Bold.otf");
        FontTypeface.setDefaultFont(this, "MONOSPACE", "fonts/proxima.otf");
        FontTypeface.setDefaultFont(this, "SERIF", "fonts/proxima-light.otf");
        FontTypeface.setDefaultFont(this, "SANS_SERIF", "fonts/comforta.ttf");

        MakaanNetworkClient.init(this);
        JarvisServiceCreator.create(this);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

        MakaanServiceFactory.getInstance().registerService(MasterDataService.class, new MasterDataService());
        MakaanServiceFactory.getInstance().registerService(ListingService.class, new ListingService());
        MakaanServiceFactory.getInstance().registerService(SearchService.class, new SearchService());
        MakaanServiceFactory.getInstance().registerService(CityService.class, new CityService());
        MakaanServiceFactory.getInstance().registerService(PyrService.class, new PyrService());
        MakaanServiceFactory.getInstance().registerService(SaveSearchService.class,new SaveSearchService());

        MakaanServiceFactory.getInstance().registerService(LocalityService.class, new LocalityService());
        MakaanServiceFactory.getInstance().registerService(PriceTrendService.class, new PriceTrendService());
        MakaanServiceFactory.getInstance().registerService(TaxonomyService.class, new TaxonomyService());
        MakaanServiceFactory.getInstance().registerService(UserService.class, new UserService());
        MakaanServiceFactory.getInstance().registerService(ImageService.class, new ImageService());
        MakaanServiceFactory.getInstance().registerService(LocationService.class, new LocationService());
        //MakaanServiceFactory.getInstance().registerService(TopAgentsByLocalityService.class , new TopAgentsByLocalityService());

        MakaanServiceFactory.getInstance().registerService(BuilderService.class, new BuilderService());
        MakaanServiceFactory.getInstance().registerService(SellerService.class, new SellerService());

        MakaanServiceFactory.getInstance().registerService(AmenityService.class, new AmenityService());

        MakaanServiceFactory.getInstance().registerService(ProjectService.class, new ProjectService());
        MakaanServiceFactory.getInstance().registerService(BlogService.class, new BlogService());
        MakaanServiceFactory.getInstance().registerService(UserLoginService.class, new UserLoginService());
        MakaanServiceFactory.getInstance().registerService(WishListService.class, new WishListService());
        MakaanServiceFactory.getInstance().registerService(AgentService.class, new AgentService());
        MakaanServiceFactory.getInstance().registerService(OtpVerificationService.class, new OtpVerificationService());
        MakaanServiceFactory.getInstance().registerService(AmenityService.class, new AmenityService());
        MakaanServiceFactory.getInstance().registerService(LeadInstantCallbackService.class, new LeadInstantCallbackService());
        MakaanServiceFactory.getInstance().registerService(ForgotPasswordService.class, new ForgotPasswordService());
        MakaanServiceFactory.getInstance().registerService(UserRegistrationService.class, new UserRegistrationService());

        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
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
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateDefaultAmenityIds();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyDisplayOrder();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBhkList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateListingInfoList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateDirectionList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateOwnershipTypeList();

        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisMessageType();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisCtaMessageType();

        CookieStore cookieStore = new MakaanCookieStore(getApplicationContext());
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        Analytics analytics = new Analytics.Builder(this, "xMHqomsTMdGwOiJByBsKUbH78Akhbaku").build();
        Analytics.setSingletonInstance(analytics);
        Analytics.with(this).identify(JarvisConstants.DELIVERY_ID);
        Analytics.with(this).flush();

        if(null!=CookiePreferences.getUserInfo(this)) {
            MasterDataCache.getInstance().setUserData(CookiePreferences.getUserInfo(this).getData());
        }

    }


}
