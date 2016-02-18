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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.ui.view.BadgeView;
import com.makaan.util.AppBus;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BuyerJourneyActivity extends AppCompatActivity {
    @Bind(R.id.button_login)
    Button mLoginButton;

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    @Bind(R.id.iv_profile_image)
    CircularImageView mProfileImage;

    @Bind(R.id.tv_username)
    TextView mUserName;

    private AlertDialog mAlertDialog;

    private static final int LOGIN_REQUEST =1001;


    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBarLayout;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.fragment_buyer_profile);
        ButterKnife.bind(BuyerJourneyActivity.this);
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
                    if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red, null));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.white, null), getResources().getColor(R.color.white, null));
                        } else {
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.buyer_dashboard_profile_background_color, null));
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red, null));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.listingBlack, null), getResources().getColor(R.color.listingBlack, null));
                        } else {
                            mAppBarLayout.setBackgroundColor(getResources().getColor(R.color.buyer_dashboard_profile_background_color));
                            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.app_red));
                            mTabLayout.setTabTextColors(getResources().getColor(R.color.listingBlack), getResources().getColor(R.color.listingBlack));
                        }
                    }
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
        }
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        if("login".equals(mLoginButton.getText().toString())) {
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {

        }
    }

    private void setUserData() {
        if (CookiePreferences.isUserLoggedIn(BuyerJourneyActivity.this)) {
//            mLoginButton.setVisibility(View.GONE);
            mLoginButton.setText("logout");
            mUserName.setText(CookiePreferences.getUserInfo(this).getData().getFirstName());
        } else {
            mLoginButton.setText("login");
            mLoginButton.setVisibility(View.VISIBLE);
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
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                //TODO
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_logout:
                //TODO have to make logout api call
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show();
                //finish();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
