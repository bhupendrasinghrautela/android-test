package com.makaan.activity.listing;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.event.trend.callback.TopLocalitiesTrendCallback;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.response.locality.Locality;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.ImageService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PriceTrendService;
import com.makaan.service.WishListService;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.JsonBuilder;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

        //((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populatePropertyAmenities();
        //((MasterDataService)(MakaanServiceFactory.getInstance().getService(MasterDataService.class))).populateMasterFunishings();


        //MakaanBuyerApplication.mSerpSelector.term("cityId", "11").term("listingCategory", new String[]{"Primary", "Resale"});
        //((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).handleSerpRequest(MakaanBuyerApplication.mSerpSelector);
        //((CityService) (MakaanServiceFactory.getInstance().getService(CityService.class))).getCityById(11L);
        //((CityService) (MakaanServiceFactory.getInstance().getService(CityService.class))).getTopLocalitiesInCity(11L, 5);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getLocalityById(50186L);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getTopBuildersInLocality(50123L, 5);
        //((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(429713L);
        List<Long> tempUsers = new ArrayList<>();
        tempUsers.add(3564144L);
        tempUsers.add(3901325L);
        //((UserService) (MakaanServiceFactory.getInstance().getService(UserService.class))).getCompanyUsers(tempUsers);
        //((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(436057L);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getNearByLocalities(12.84112072, 77.66799164, 5);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getTrendingSearchesInLocality(50175L);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getTrendingSearchesInLocality(50175L);
        //((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class))).getTopAgentsForLocality(2L, 50175L, 5, true, new TopBuyAgentsInLocalityCallback());

        ArrayList<Long> topLocalities = new ArrayList<>();
        topLocalities.add(53099L);
        topLocalities.add(53130L);
        topLocalities.add(53099L);
        topLocalities.add(53476L);
        topLocalities.add(53477L);
        //((PriceTrendService) (MakaanServiceFactory.getInstance().getService(PriceTrendService.class))).getPriceTrendForLocalities(topLocalities, 6, new TopLocalitiesTrendCallback());
        //((CityService) (MakaanServiceFactory.getInstance().getService(CityService.class))).getPropertyRangeInCity(11L, null,null,false ,10000, 500000, 12250);
        //((CityService) (MakaanServiceFactory.getInstance().getService(CityService.class))).getPropertyRangeInCity(11L, null,null,true ,10000, 500000, 12250);
        //((BuilderService) (MakaanServiceFactory.getInstance().getService(BuilderService.class))).getBuilderById(100002L);
        //((LocalityService) (MakaanServiceFactory.getInstance().getService(LocalityService.class))).getGooglePlaceDetail("ChIJAQAA8UjkDDkRmdprFuRlLbo");
        //((ProjectService) (MakaanServiceFactory.getInstance().getService(ProjectService.class))).getProjectById(506147L);
        //((ProjectService) (MakaanServiceFactory.getInstance().getService(ProjectService.class))).getProjectConfiguration(506147L);
        //((ProjectService) (MakaanServiceFactory.getInstance().getService(ProjectService.class))).getSimilarProjects(506147L,10);
        //((BlogService) (MakaanServiceFactory.getInstance().getService(BlogService.class))).getBlogs("Home");
        //((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getOtherSellersOnListingDetail(654368L,3,3,0,0,0,5);
        //((PriceTrendService) (MakaanServiceFactory.getInstance().getService(PriceTrendService.class))).getPriceTrendForProject(500773L,12);
        ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(562194L);



        //((AgentService) (MakaanServiceFactory.getInstance().getService(AgentService.class))).getTopAgentsForLocality(2L, 50175L, 5, false, new TopRentAgentsInLocalityCallback());

        /*Intent intent = new Intent(this, PropertyActivity.class);
        startActivity(intent);*/



        //testWishList();

    }

    private void testWishList(){
        if(!CookiePreferences.isUserLoggedIn(this)) {
            UserLoginService userLoginService =
                    (UserLoginService) MakaanServiceFactory.getInstance().getService(UserLoginService.class);
            userLoginService.loginWithMakaanAccount("harvi@proptiger.com", "123456");
        }else{
            WishListService wishListService =
                    (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
            wishListService.get();
        }
    }


    @Subscribe
    public void onResults(SerpGetEvent serpGetEvent) {
        System.out.println("TEST");
    }

    @Subscribe
    public void onResults(CityTopLocalityEvent cityTopLocalityEvent) {
        System.out.println("TEST 1");

        ArrayList<Long> topLocalities = new ArrayList<Long>();

        for (Locality locality : cityTopLocalityEvent.topLocalitiesInCity) {
            topLocalities.add(locality.localityId);
        }
        ((PriceTrendService) (MakaanServiceFactory.getInstance().getService(PriceTrendService.class))).getPriceTrendForLocalities(topLocalities, 6, new TopLocalitiesTrendCallback());

    }

    @Subscribe
    public void onResults(UserLoginEvent userLoginEvent) {

        Toast.makeText(this,userLoginEvent.userResponse.getData().firstName, Toast.LENGTH_SHORT).show();
        try {
            CookiePreferences.setUserInfo(this,
                    JsonBuilder.toJson(userLoginEvent.userResponse).toString());
            CookiePreferences.setUserLoggedIn(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onResults(WishListResultEvent wishListResultEvent) {
        WishListResponse response = wishListResultEvent.wishListResponse;
        Toast.makeText(this,"Wish list  - " + response.totalCount, Toast.LENGTH_SHORT).show();

    }


    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event){
        displayPopupWindow(event.message);
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }
}
