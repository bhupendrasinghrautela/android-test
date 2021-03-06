package com.makaan.activity.userLogin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.fragment.userLogin.LoginSocialFragment;
import com.makaan.fragment.userLogin.ReplaceFragment;
import com.makaan.fragment.userLogin.SignUpFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.network.VolleyErrorParser;
import com.makaan.response.ResponseError;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.response.login.OnUserRegistrationListener;
import com.makaan.response.login.UserLoginPresenter;

import com.makaan.response.user.UserResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.service.user.OnFacebookTokenListener;
import com.makaan.ui.CommonProgressDialog;
import com.makaan.util.AppBus;
import com.segment.analytics.Properties;


/**
 * Created by sunil on 29/12/15.
 */
public class UserLoginActivity extends BaseJarvisActivity implements ReplaceFragment, OnUserLoginListener,OnUserRegistrationListener {

    private FragmentTransaction mFragmentTransaction;
    private UserLoginPresenter mUserLoginPresenter;
    private CommonProgressDialog mProgressDialog;
    public static final String PLEASE_WAIT="Please wait...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*--------------------track--------------code-------------*/
        Properties properties1 = MakaanEventPayload.beginBatch();
        properties1.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_LOGIN);
        properties1.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_LOGIN);
        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.screenName);
        /*--------------------------------------------------------*/


        setContentView(R.layout.user_login_activity);
        Intent intent = getIntent();
        int loginType = UserLoginPresenter.LOGIN_NONE;
        if(intent != null && intent.hasExtra(UserLoginPresenter.LOGIN_TYPE)) {
            loginType = intent.getIntExtra(UserLoginPresenter.LOGIN_TYPE, UserLoginPresenter.LOGIN_NONE);

        }
        mProgressDialog =new CommonProgressDialog();
        mUserLoginPresenter = new UserLoginPresenter(this, this, this, this);
        mUserLoginPresenter.showLoginChooserFragment(loginType);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {

        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onUserLoginSuccess(UserResponse userResponse, String response) {
        if(isActivityDead()){
            return;
        }

        cancelProgressDialog();
        CookiePreferences.setUserInfo(this, response);
        CookiePreferences.setUserLoggedIn(this);
        refreshWishList();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onUserLoginError(ResponseError error) {
        if(isActivityDead()){
            return;
        }

        cancelProgressDialog();
        if(error!=null && error.error!=null ) {
            Toast.makeText(this, VolleyErrorParser.getMessage(error.error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserLoginBegin() {
        if(isActivityDead()){
            return;
        }

        mProgressDialog.showDialog(this, PLEASE_WAIT);
    }

    @Override
    public void onUserLoginCancel() {
        if(isActivityDead()){
            return;
        }

        cancelProgressDialog();
    }

    @Override
    public void onBackPressed() {
        SignUpFragment myFragment = (SignUpFragment)getSupportFragmentManager().findFragmentByTag(SignUpFragment.class.getName());
        LoginSocialFragment myFragmnt = (LoginSocialFragment)getSupportFragmentManager().findFragmentByTag(LoginSocialFragment.class.getName());

        if(myFragment!=null && myFragment.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.closeSignUp);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.signUpClose);
            myFragment=null;
        }

        if(myFragmnt!=null && myFragmnt.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.closeSocial);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.signUpSocial);
            myFragmnt=null;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUserRegistrationSuccess(UserResponse userResponse, String response) {
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.userLogin);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.registrationSuccess);
        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.login);
        cancelProgressDialog();
        /*CookiePreferences.setUserInfo(this, response);
        CookiePreferences.setUserLoggedIn(this);
        refreshWishList();
        setResult(RESULT_OK);
        finish();*/
    }

    @Override
    public void onUserRegistrationError(ResponseError error) {
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.userLogin);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.registrationFailed);
        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.login);
        cancelProgressDialog();
        Toast.makeText(this,error.msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBus.getInstance().register(this);
    }

    @Override
    public void onUserRegistrationBegin() {
        mProgressDialog.showDialog(this, PLEASE_WAIT);
    }

    private void refreshWishList(){
        WishListService wishListService =
                (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
        wishListService.get();
    }

    private void cancelProgressDialog(){
        if(mProgressDialog !=null)
            mProgressDialog.dismissDialog();
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return ScreenNameConstants.SCREEN_NAME_LOGIN;
    }

}
