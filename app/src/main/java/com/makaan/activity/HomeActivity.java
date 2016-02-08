package com.makaan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.constants.PreferenceConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.cookie.Session;
import com.makaan.event.location.LocationGetEvent;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.LocationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.JsonBuilder;
import com.makaan.util.Preference;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

public class HomeActivity extends MakaanBaseSearchActivity {
    boolean isBottom = true;
    private Toolbar mToolbar;

    RelativeLayout rlSearch;
    RadioGroup rgType;
    TextView tvSearch;
    FrameLayout topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationService service = (LocationService) MakaanServiceFactory.getInstance().getService(LocationService.class);
        service.getUserLocation();

        topBar=(FrameLayout) findViewById(R.id.top_bar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tvSearch =(TextView) findViewById(R.id.activity_home_search_text_view);
        tvSearch.setFocusable(false);
        rgType=(RadioGroup) findViewById(R.id.activity_home_property_type_radio_group);
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
                        Preference.putInt(editor, PreferenceConstants.PREF_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
                        mSerpContext = SERP_CONTEXT_RENT;
                        break;
                }

                setShowSearchBar(true, false);
                setSearchViewVisibility(true);

                /*Intent intent = new Intent(HomeActivity.this, SerpActivity.class);
                intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_HOME);
                SharedPreferences.Editor editor = getSharedPreferences(PreferenceConstants.PREF, MODE_PRIVATE).edit();

                SerpRequest request = new SerpRequest();
                switch (rgType.getCheckedRadioButtonId()) {
                    case R.id.activity_home_property_buy_radio_button:
                        Preference.putInt(editor, PreferenceConstants.PREF_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
                        request.setSerpContext(SerpRequest.CONTEXT_BUY);
                        break;
                    case R.id.activity_home_property_rent_radio_button:
                        request.setSerpContext(SerpRequest.CONTEXT_RENT);
                        break;
                }
                request.launchSerp(HomeActivity.this, SerpActivity.TYPE_HOME);*/
            }
        });

        testWishList();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.activity_home_property_buy_radio_button: {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) buyRadioButton.getLayoutParams();
                        params.setMargins(0, 0, 0, 0);
                        buyRadioButton.setLayoutParams(params);

                        params = (LinearLayout.LayoutParams) rentRadioButton.getLayoutParams();
                        params.setMargins(0, 10, 0, 0);
                        rentRadioButton.setLayoutParams(params);
                        break;
                    }
                    case R.id.activity_home_property_rent_radio_button: {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rentRadioButton.getLayoutParams();
                        params.setMargins(0, 0, 0, 0);
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

        /*rlSearch = (RelativeLayout) findViewById(R.id.rl_footer);
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBottom) {
                    slideToTop();
                    isBottom = false;
                } else {
                    slideToBottom();
                    isBottom = true;
                }


            }
        });*/

        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        /*final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
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
    public void onResults(UserLoginEvent userLoginEvent) {

        if(null!=userLoginEvent.error){
            return;
        }

        Toast.makeText(this, userLoginEvent.userResponse.getData().firstName, Toast.LENGTH_SHORT).show();
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
        if(null==wishListResultEvent || null!=wishListResultEvent.error){
            return;
        }
        WishListResponse response = wishListResultEvent.wishListResponse;
//        Toast.makeText(this,"Wish list  - " + response.totalCount, Toast.LENGTH_SHORT).show(); causing null pointer

    }

    @Subscribe
    public void onResults(LocationGetEvent locationGetEvent) {
        Session.myLocation = locationGetEvent.myLocation;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    /*public void slideToTop() {
        Animation slide;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -3.2f);

        slide.setDuration(500);
        rlSearch.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlSearch.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                rlSearch.setLayoutParams(lp);
                mToolbar.setVisibility(View.GONE);
                rgType.setVisibility(View.GONE);
                rlSearch.setVisibility(View.GONE);
                topBar.setVisibility(View.VISIBLE);
                //tvSearch.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //tvSearch.setFocusableInTouchMode(true);

            }
        });
    }

    public void slideToBottom() {
        Animation slide;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 3.2f);

        slide.setDuration(500);
        rlSearch.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                rlSearch.clearAnimation();

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(25, 25, 0, 74);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlSearch.setLayoutParams(lp);
                mToolbar.setVisibility(View.VISIBLE);
                rgType.setVisibility(View.VISIBLE);

            }
        });
    }

    private void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if(shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }*/

    @Override
    protected void setSearchViewVisibility(boolean visible) {
        super.setSearchViewVisibility(visible);
        if(!visible) {
            setShowSearchBar(false, false);
        }
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
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
}
