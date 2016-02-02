package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.makaan.R;
import com.makaan.ui.view.BadgeView;
import com.pkmmte.view.CircularImageView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BuyerJourneyActivity extends AppCompatActivity {

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    @Bind(R.id.iv_profile_image)
    CircularImageView mProfileImage;

    private AlertDialog mAlertDialog;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;

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

    }

    private void setUserData() {
        //UserInfo userInfo = Preferences.getUserInfo(this);
        //mProfileImage.setImageURI(Uri.parse(userInfo.getData().getProfileImageUrl()));
        //mCollapsingToolbar.setTitle("User");
    }

    private void setupAppBar() {
        setSupportActionBar(mToolbar);

        //set up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

    }

    private void initViews() {

        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Journey.value));
        mTabLayout.addTab(mTabLayout.newTab().setText(TabType.Notifications.value));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final BuyerJourneyPagerAdapter adapter = new BuyerJourneyPagerAdapter
                (getSupportFragmentManager(), mTabLayout.getTabCount());

        mViewPager.setAdapter(adapter);

        BadgeView badge=new BadgeView(this,mTabLayout);
        badge.setText("22");
        badge.show();
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
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                //TODO
                break;
            case R.id.item_logout:
                //TODO
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
