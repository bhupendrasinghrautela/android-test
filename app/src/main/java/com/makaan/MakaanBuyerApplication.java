package com.makaan;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.LruBitmapCache;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.cookie.MakaanCookieStore;
import com.makaan.jarvis.ChatHistoryService;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.jarvis.JarvisServiceCreator;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.notification.GcmRegister;
import com.makaan.pojo.SerpObjects;
import com.makaan.service.AgentService;
import com.makaan.service.AmenityService;
import com.makaan.service.BlogService;
import com.makaan.service.BuilderService;
import com.makaan.service.CityService;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.ImageService;
import com.makaan.service.LeadInstantCallbackService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.LocationService;
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
import com.makaan.service.download.DownloadAssetService;
import com.makaan.service.user.ForgotPasswordService;
import com.makaan.service.user.UserLoginService;
import com.makaan.service.user.UserLogoutService;
import com.makaan.service.user.UserRegistrationService;
import com.makaan.util.FontTypeface;
import com.makaan.util.RandomString;
import com.segment.analytics.Analytics;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

import io.fabric.sdk.android.Fabric;

//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by vaibhav on 23/12/15.
 */
public class MakaanBuyerApplication extends Application {
    public static final String TAG = MakaanBuyerApplication.class.getSimpleName();


    public static RandomString randomString = new RandomString(6);
//    public static Selector mSerpSelector = new Selector();
    public static SparseArray<SerpObjects> serpObjects = new SparseArray<>();
    public static LruBitmapCache bitmapCache;

    public static Gson gson;
/*    private static RefWatcher refWatcher;

    public static RefWatcher getRefWatcher() {
        //MakaanBuyerApplication application = (MakaanBuyerApplication) context.getApplicationContext();
        return refWatcher;
    }*/

    public static LruBitmapCache getBitmapCache() {
        return bitmapCache;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MakaanNetworkClient.init(this);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
        MakaanServiceFactory.getInstance().registerService(ChatHistoryService.class, new ChatHistoryService());
        GcmRegister.checkAndSetGcmId(this,null);

        Fabric.with(this, new Crashlytics());
        bitmapCache = new LruBitmapCache();

        // leak canary to detect memory leaks
        // comment below line to disable leak canary
        //refWatcher = LeakCanary.install(this, TemporaryLeakUploadService.class, AndroidExcludedRefs.createAppDefaults().build());

        FontTypeface.setDefaultFont(this, "DEFAULT", "fonts/ProximaNova-Bold.otf");
        FontTypeface.setDefaultFont(this, "MONOSPACE", "fonts/proxima.otf");
        FontTypeface.setDefaultFont(this, "SERIF", "fonts/proxima-light.otf");
        FontTypeface.setDefaultFont(this, "SANS_SERIF", "fonts/comforta.ttf");




        //GcmRegister.checkAndSetGcmId(this, null);

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
        MakaanServiceFactory.getInstance().registerService(UserLogoutService.class, new UserLogoutService());

        MakaanServiceFactory.getInstance().registerService(WishListService.class, new WishListService());
        MakaanServiceFactory.getInstance().registerService(AgentService.class, new AgentService());
        MakaanServiceFactory.getInstance().registerService(OtpVerificationService.class, new OtpVerificationService());
        MakaanServiceFactory.getInstance().registerService(AmenityService.class, new AmenityService());
        MakaanServiceFactory.getInstance().registerService(LeadInstantCallbackService.class, new LeadInstantCallbackService());
        MakaanServiceFactory.getInstance().registerService(ForgotPasswordService.class, new ForgotPasswordService());
        MakaanServiceFactory.getInstance().registerService(UserRegistrationService.class, new UserRegistrationService());
        MakaanServiceFactory.getInstance().registerService(AnalyticsService.class, new AnalyticsService());
        MakaanServiceFactory.getInstance().registerService(ClientLeadsService.class, new ClientLeadsService());
        MakaanServiceFactory.getInstance().registerService(ClientEventsService.class, new ClientEventsService());
        MakaanServiceFactory.getInstance().registerService(ChatHistoryService.class, new ChatHistoryService());

        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateApiLabels();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyStatus();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateRentPropertyTypes();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateFilterGroupsRent();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePyrGroupsBuy();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePyrGroupsRent();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateLocalityAmenityMap();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateProjectAmenityMap();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateSearchType();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyAmenities();
        ((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateMasterFunishings();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateDefaultAmenityIds();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyDisplayOrder();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBhkList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateListingInfoList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateDirectionList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateOwnershipTypeList();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateConstructionStatus();

        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisMessageType();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisCtaMessageType();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateJarvisSerpFilterMessageMap();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateBuyerJourneyMessageMap();
        ((MasterDataService) (MakaanServiceFactory.getInstance().getService(MasterDataService.class))).checkMandatoryVersion(this);

        CookieStore cookieStore = new MakaanCookieStore(getApplicationContext());
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        if(!TextUtils.isEmpty(JarvisConstants.DELIVERY_ID)) {
            Analytics analytics = new Analytics.Builder(this, MakaanTrackerConstants.segmentSdkKey).build();
            Analytics.setSingletonInstance(analytics);
            Analytics.with(this).identify(JarvisConstants.DELIVERY_ID);
            Analytics.with(this).flush();
        }

        if(null!=CookiePreferences.getUserInfo(this)) {
            MasterDataCache.getInstance().setUserData(CookiePreferences.getUserInfo(this).getData());
        }

        JarvisServiceCreator.create(this);

        // TODO verify after update
//        setPeriodicUpdateRequest();

    }

    private void setPeriodicUpdateRequest() {

        SharedPreferences preferences = getSharedPreferences(PreferenceConstants.PREF, MODE_PRIVATE);
        Boolean firstTime = preferences.getBoolean(PreferenceConstants.PREF_FIRST_TIME, true);

        if(firstTime) {

//            Log.d("DEBUG", "setPeriodicUpdateRequest");
            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(getApplicationContext(), DownloadAssetService.class);
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getService(this, DownloadAssetService.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            long firstMillis = System.currentTimeMillis();
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                    60 * 1000, pIntent);
            preferences.edit().putBoolean(PreferenceConstants.PREF_FIRST_TIME, false).apply();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        bitmapCache.evictAll();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_COMPLETE:
            case TRIM_MEMORY_RUNNING_CRITICAL:
                bitmapCache.evictAll();
                break;
        }
    }
}
