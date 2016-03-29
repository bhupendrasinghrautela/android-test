package com.makaan.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerJourneyActivity;
import com.makaan.constants.PreferenceConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.cookie.Session;
import com.makaan.event.location.LocationGetEvent;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.notification.GcmRegister;
import com.makaan.pojo.VersionUpdate;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.user.UserResponse;
import com.makaan.service.LocationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.service.WishListService;
import com.makaan.util.CommonPreference;
import com.makaan.util.JsonParser;
import com.makaan.util.PermissionManager;
import com.makaan.util.Preference;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;

public class HomeActivity extends MakaanBaseSearchActivity {
    boolean isBottom = true;
    private Toolbar mToolbar;

    RelativeLayout rlSearch;
    RadioGroup rgType;
    TextView tvSearch;
    FrameLayout topBar;

    @Bind(R.id.makaan_toolbar_login_text_view)
    TextView tvUserName;
    @Bind(R.id.makaan_toolbar_profile_icon_image_view)
    ImageView mImageViewBuyer;
    @Bind(R.id.makaan_toolbar_profile_icon_text_view)
    TextView  mTextViewBuyerInitials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppsFlyerLib.getInstance().init(this,getString(R.string.app_appsflyer_id));

        PermissionManager.begin().addRequest(PermissionManager.ACCOUNTS_REQUEST).request(this);

        GcmRegister.checkAndSetGcmId(this, null);

        LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
        service.getUserLocation();

        checkForVersionUpdate();
        topBar=(FrameLayout) findViewById(R.id.top_bar);

        topBar = (FrameLayout) findViewById(R.id.top_bar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tvSearch = (TextView) findViewById(R.id.activity_home_search_text_view);
        tvSearch.setFocusable(false);
        rgType = (RadioGroup) findViewById(R.id.activity_home_property_type_radio_group);
        final RadioButton buyRadioButton = (RadioButton) findViewById(R.id.activity_home_property_buy_radio_button);
        final RadioButton rentRadioButton = (RadioButton) findViewById(R.id.activity_home_property_rent_radio_button);


        View searchView = findViewById(R.id.activity_home_search_relative_view);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences(PreferenceConstants.PREF, MODE_PRIVATE).edit();
                switch (rgType.getCheckedRadioButtonId()) {
                    case R.id.activity_home_property_buy_radio_button:
                        mSerpContext = SERP_CONTEXT_BUY;
                        Preference.putInt(editor, PreferenceConstants.PREF_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
                        break;
                    case R.id.activity_home_property_rent_radio_button:
                        Preference.putInt(editor, PreferenceConstants.PREF_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_RENT);
                        mSerpContext = SERP_CONTEXT_RENT;
                        break;
                }
                editor.commit();

                setShowSearchBar(true, false);
                setSearchViewVisibility(true);
            }
        });

        if(CookiePreferences.isUserLoggedIn(this)){
            setUserData();
            WishListService wishListService =
                    (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
            wishListService.get();
        }else{

        }

        // get saved searches
        SaveSearchService saveSearchService =
                (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
        saveSearchService.getSavedSearches();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.activity_home_property_buy_radio_button: {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buyRadioButton.getLayoutParams();
                        params.setMargins(0, 0, 1, 0);
                        buyRadioButton.setLayoutParams(params);

                        params = (LinearLayout.LayoutParams) rentRadioButton.getLayoutParams();
                        params.setMargins(0, 10, 0, 0);
                        rentRadioButton.setLayoutParams(params);
                        break;
                    }
                    case R.id.activity_home_property_rent_radio_button: {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rentRadioButton.getLayoutParams();
                        params.setMargins(0, 0, 1, 0);
                        rentRadioButton.setLayoutParams(params);

                        params = (LinearLayout.LayoutParams) buyRadioButton.getLayoutParams();
                        params.setMargins(0, 10, 0, 0);
                        buyRadioButton.setLayoutParams(params);
                        break;
                    }
                }
            }
        });

        initUi(false);
    }

    private void checkForVersionUpdate() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionUpdate versionUpdate = (VersionUpdate) JsonParser.parseJson(CommonPreference.getMandatoryVersion(this),VersionUpdate.class);
            if(versionUpdate!=null && pInfo.versionCode< versionUpdate.getMandatoryVersionCode()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(versionUpdate.getMessage());
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.upgrade), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
            }
            else if(versionUpdate!= null && pInfo.versionCode<versionUpdate.getCurrentVersionCode()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(versionUpdate.getMessage());
                builder.setPositiveButton(getString(R.string.upgrade), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        }catch (Exception e){}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode & PermissionManager.ACCOUNTS_REQUEST)
                == PermissionManager.ACCOUNTS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GcmRegister.checkAndSetGcmId(this, null);
            } else if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // TODO show message or something
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserData();
    }

    private void setUserData() {
        UserResponse info=CookiePreferences.getUserInfo(this);
        if(null!=info && null!=info.getData()) {
            tvUserName.setVisibility(View.GONE);
            //tvUserName.setText(info.getData().getFirstName());
            mTextViewBuyerInitials.setText(info.getData().getFirstName());
            mTextViewBuyerInitials.setVisibility(View.VISIBLE);
            mImageViewBuyer.setVisibility(View.GONE);
        }else{
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(R.string.login);
            mTextViewBuyerInitials.setVisibility(View.GONE);
            mImageViewBuyer.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onResults(UserLoginEvent userLoginEvent) {
        if(isActivityDead()){
            return;
        }

        if (null != userLoginEvent.error) {
            return;
        }
        setUserData();

    }

    @Subscribe
    public void onResults(LocationGetEvent locationGetEvent) {
        if(isActivityDead()){
            return;
        }
        Session.apiLocation = locationGetEvent.myLocation;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return "Home";
    }


    @Override
    protected void setSearchViewVisibility(boolean visible) {
        super.setSearchViewVisibility(visible);
        if (!visible) {
            setShowSearchBar(false, false);
        }
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        if(isActivityDead()){
            return;
        }

        super.onResults(searchResultEvent);
    }

    @Override
    protected boolean needScrollableSearchBar() {
        return false;
    }

    @Override
    protected boolean supportsListing() {
        return false;
    }

    @OnClick(R.id.linear_layout_makaan_toolbar_login)
    public void onLoginCLick() {
//        if(CookiePreferences.isUserLoggedIn(this)){
        Intent intent = new Intent(HomeActivity.this, BuyerJourneyActivity.class);
        startActivity(intent);
//        }else{
//            Intent intent = new Intent(HomeActivity.this, UserLoginActivity.class);
//            startActivity(intent);
//        }
    }
}
