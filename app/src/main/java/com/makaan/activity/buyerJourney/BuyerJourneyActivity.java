package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.user.UserLogoutEvent;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.buyerjourney.PhaseChange;
import com.makaan.response.listing.Listing;
import com.makaan.response.user.UserResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.UserLogoutService;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class BuyerJourneyActivity extends MakaanFragmentActivity {
    @Bind(R.id.button_login)
    Button mLoginButton;

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    @Bind(R.id.iv_profile_image)
    CircleImageView mProfileImage;

    @Bind(R.id.tv_username)
    TextView mUserName;
    @Bind(R.id.tv_subtitle)
    TextView mSubtitle;

    private AlertDialog mAlertDialog;

    private static final int LOGIN_REQUEST =1001;


    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    private Long mAgentId;
    private Long mLeadId;

    enum TabType {
        Journey("journey"),
        Notifications("notifications");
        String value;

        TabType(String value) {
            this.value = value;
        }
    }

    public final String TAG = BuyerJourneyActivity.class.getSimpleName();

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return "Buyer journey";
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_buyer_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
//        setContentView(R.layout.fragment_buyer_profile);
        initViews();

        setupAppBar();
        setUserData();
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int toolbarOffset = mAppBarLayout.getTotalScrollRange();
                if (verticalOffset + toolbarOffset == 0) {
                    mCollapsingToolbar.setCollapsedTitleTextColor(0xFFFFFFFF);
                    if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
                        mCollapsingToolbar.setTitle(CookiePreferences.getUserInfo(BuyerJourneyActivity.this).getData().getFirstName());
                    } else {
                        mCollapsingToolbar.setTitle("guest user");
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.app_red, null));
                        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white, null));
                        mTabLayout.setTabTextColors(getResources().getColor(R.color.white, null), getResources().getColor(R.color.white, null));
                    } else {
                        mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.app_red));
                        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                        mTabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
                    }

                } else {
                    mCollapsingToolbar.setTitle("");
                    /*if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red, null));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.white, null), getResources().getColor(R.color.white, null));
                        } else {
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
                        }
                    } else {*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.buyer_dashboard_profile_background_color, null));
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red, null));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.listingBlack, null), getResources().getColor(R.color.listingBlack, null));
                        } else {
                            mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.buyer_dashboard_profile_background_color));
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.listingBlack), getResources().getColor(R.color.listingBlack));
                        }
//                    }
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST && resultCode == RESULT_OK){
            setUserData();
            initViews();
            invalidateOptionsMenu();
        }
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        if("login".equals(mLoginButton.getText().toString())) {
            String screenName=this.getIntent().getExtras().getString("screenName");
            switch(screenName){
                case "Project":{
                    Properties properties= MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.login);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProject);
                    break;
                }
                case "Listing detail":{
                    Properties properties=MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.login);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProperty);
                    break;
                }
                case "serp":{
                    Properties properties=MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.login);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerp);
                    break;
                }
            }

            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {
            try {
                onLogoutClick();
            } catch (Exception e) {
                Toast.makeText(BuyerJourneyActivity.this,
                        getResources().getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUserData() {
        if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
//            mLoginButton.setVisibility(View.GONE);
            mLoginButton.setText("logout");
            UserResponse userResponse = CookiePreferences.getUserInfo(this);
            if(userResponse != null) {
                UserResponse.UserData userData = userResponse.getData();
                if(userData != null) {
                    mUserName.setText(userData.getFirstName());
                    if (!TextUtils.isEmpty(userData.getProfileImageUrl())) {
                        MakaanNetworkClient.getInstance().getImageLoader().get(userData.profileImageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }

                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                        if (b && imageContainer.getBitmap() == null) {
                                            return;
                                        }
                                        if(mProfileImage != null) {
                                            mProfileImage.setImageBitmap(imageContainer.getBitmap());
                                        }
                                    }
                                }
                        );
                    }
                    mSubtitle.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            mLoginButton.setText("login");
            mLoginButton.setVisibility(View.VISIBLE);
            mSubtitle.setVisibility(View.VISIBLE);
        }
    }

    private void setupAppBar() {
        setSupportActionBar(mToolbar);

        //set up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.back_black);
        actionBar.setTitle("");

    }

    private void initViews() {

        //TODO need to refresh this layout based on
        mTabLayout.removeAllTabs();

        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Journey.value));
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Notifications.value));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final BuyerJourneyPagerAdapter adapter = new BuyerJourneyPagerAdapter
                (getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);

        //TODO show notification count here
/*        BadgeView badge = new BadgeView(this, mTabLayout);
        badge.setText("5");
        badge.show();*/
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
            getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
            mUserName.setText(CookiePreferences.getUserInfo(this).getData().getFirstName());
            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.item_settings:
                Intent intent = new Intent(this, BuyerAccountSettingActivity.class);
                startActivity(intent);
                break;*/
            case R.id.item_logout:
                try{
                    onLogoutClick();
                }catch (Exception e){
                    Toast.makeText(BuyerJourneyActivity.this,getResources()
                            .getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void onLogoutClick() {
        UserLogoutService userLogoutService =
                (UserLogoutService) MakaanServiceFactory.getInstance()
                        .getService(UserLogoutService.class);
        userLogoutService.makeLogoutRequest();
    }

    @Subscribe
    public void onLogoutResult(UserLogoutEvent userLogoutEvent){
        if(null!=userLogoutEvent && userLogoutEvent.isLogoutSuccessfull()){
            CookiePreferences.setUserLoggedOut(this);
            CookiePreferences.setUserInfo(this,null);
            //TODO also clear metadata and userinfo
            finish();
        }else{
            Toast.makeText(this,getResources().getString(R.string.generic_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent){
        if(null == clientLeadsByGetEvent || null != clientLeadsByGetEvent.error){
            return;
        }
        if(clientLeadsByGetEvent.results != null && clientLeadsByGetEvent.results.size() > 0) {
            mAgentId = clientLeadsByGetEvent.results.get(0).companyId;
            mLeadId = clientLeadsByGetEvent.results.get(0).id;
            if(clientLeadsByGetEvent.results.get(0).clientActivity != null) {
                trackBuyerJourney(clientLeadsByGetEvent.results.get(0).clientActivity.phaseId);
            }
        }

    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Subscribe
    public void onExposeMessage(OnExposeEvent event) {
        if(null==event.message){
            return;
        }

        if(event.message.properties == null) {
            event.message.properties = new ExposeMessage.Properties();
            event.message.properties.leadId = mLeadId;
            event.message.properties.agentId = mAgentId;
        } else {
            event.message.properties.leadId = mLeadId;
            event.message.properties.agentId = mAgentId;
        }
        displayPopupWindow(event.message);
    }

}
