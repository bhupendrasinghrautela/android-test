package com.makaan.fragment.userLogin;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.android.volley.VolleyError;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.notification.GcmRegister;
import com.makaan.response.ResponseError;
import com.makaan.response.login.OnLoginWithMakaanSelectedListener;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.response.login.UserLoginPresenter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.FacebookTokenInteractor;
import com.makaan.service.user.GoogleTokenInteractor;
import com.makaan.service.user.OnFacebookTokenListener;
import com.makaan.service.user.OnGoogleTokenListener;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.CommonUtil;
import com.makaan.util.NetworkUtil;
import com.makaan.util.PermissionManager;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by root on 29/12/15.
 */
public class LoginSocialFragment extends MakaanBaseFragment implements OnGoogleTokenListener,OnFacebookTokenListener {

    @Bind(R.id.fb_login)
    ImageView mFbLogin;

    @Bind(R.id.gmail_login)
    ImageView mGmailLogin;

    @Bind(R.id.makaan_login)
    TextView mNoAccount;

    private static final int GOOGLE_ACCOUNT_PICKER_CODE = 10001;

    private OnUserLoginListener mOnUserLoginListener;
    private OnLoginWithMakaanSelectedListener mOnLoginWithMakaanSelectedListener;
    private FacebookTokenInteractor mFacebookTokenInteractor;
    private int mLoginType;
    private int loginType;

    @Override
    protected int getContentViewId() {
        return R.layout.login_social;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFacebookTokenInteractor = new FacebookTokenInteractor(getActivity(), this);
        mFacebookTokenInteractor.initFacebookSdk(savedInstanceState);
        parseLoginType(mLoginType);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFacebookTokenInteractor != null) {
            mFacebookTokenInteractor.stopTracking();
        }
    }

    public void bindView(OnLoginWithMakaanSelectedListener listener, OnUserLoginListener onUserLoginListener, int loginType){
        mOnLoginWithMakaanSelectedListener = listener;
        mOnUserLoginListener = onUserLoginListener;
        mLoginType = loginType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookTokenInteractor.onFacebookActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_ACCOUNT_PICKER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                GoogleTokenInteractor interactor = new GoogleTokenInteractor(getActivity(), LoginSocialFragment.this);
                interactor.requestGoogleAccessToken(accountName);
            }else{
            }
        }
    }

    @OnClick(R.id.fb_login)
    public void onFacebookLoginClick(){
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            return;
        }
        loginType=UserLoginPresenter.LOGIN_FB;
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.facebook);
        MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.signUpSocial);
        /*if(AccessToken.getCurrentAccessToken()==null) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile", "user_friends"));
        }else {
            ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithFacebookAccount(AccessToken.getCurrentAccessToken().getToken());
        }*/

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
    }

    @OnClick(R.id.gmail_login)
    public void onGoogleLoginClick(){
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            return;
        }
        if(PermissionManager.isPermissionRequestRequired(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
            PermissionManager.begin().addRequest(PermissionManager.ACCOUNTS_REQUEST).request(getActivity());
        } else {
            Properties properties= MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.google);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.signUpSocial);
            loginType = UserLoginPresenter.LOGIN_GMAIL;
            try {
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]
                        {GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);

                startActivityForResult(intent, GOOGLE_ACCOUNT_PICKER_CODE);
            } catch (Exception e) {
                Crashlytics.logException(e);
                CommonUtil.TLog("exception", e);
            }
        }
    }

    @OnClick(R.id.makaan_login)
    public void onMakaanLoginClick(){
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.dontHaveSocial);
        MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.signUpSocial);
        mOnLoginWithMakaanSelectedListener.onLoginWithMakaanSelected();

    }

    @Override
    public void onGoogleTokenSuccess(String token) {
        mOnUserLoginListener.onUserLoginBegin();
        ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
        ))).loginWithGoogleAccount(token);

    }

    @Override
    public void onGoogleTokenFail() {

    }

    @Override
    public void onFacebookTokenSuccess(String token) {
        if(!isVisible()) {
            return;
        }

        if(null!=mOnUserLoginListener) {
            mOnUserLoginListener.onUserLoginBegin();
            ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithFacebookAccount(token);
        }
    }

    @Override
    public void onFacebookTokenFail(Exception exception) {
        Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();

        if(!isVisible()){
            return;
        }

        if(null != exception) {
            if (exception instanceof FacebookAuthorizationException) {
                LoginManager.getInstance().logOut();
                LoginManager.getInstance().logInWithReadPermissions(
                        this, Arrays.asList("email", "public_profile", "user_friends"));
                return;
            }
        }

        if(null == mOnUserLoginListener){
            return;
        }

        ResponseError responseError = new ResponseError();
        responseError.error = new VolleyError();

        mOnUserLoginListener.onUserLoginError(responseError);
    }

    @Override
    public void onFacebookCancel() {
        if(!isVisible()) {
            return;
        }

        if(null!=mOnUserLoginListener) {
            mOnUserLoginListener.onUserLoginCancel();
        }

    }

    private void parseLoginType(int loginType){
        switch (loginType){
            case UserLoginPresenter.LOGIN_FB:
                onFacebookLoginClick();
                break;
            case UserLoginPresenter.LOGIN_GMAIL:
                onGoogleLoginClick();
                break;
            case UserLoginPresenter.LOGIN_MAKAAN:
                onMakaanLoginClick();
                break;
            default:
                break;
        }
        mLoginType = 0;
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent){
        if(!isVisible()) {
            return;
        }
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY ,MakaanTrackerConstants.Category.userLogin);

        if(userLoginEvent.error!=null){
            switch (loginType){
                case UserLoginPresenter.LOGIN_FB:
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.facebookFailed);
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
                    break;
                case UserLoginPresenter.LOGIN_GMAIL:
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.googleFail);
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
                    break;
            }

            mOnUserLoginListener.onUserLoginError(userLoginEvent.error);
        } else {
            switch (loginType){
                case UserLoginPresenter.LOGIN_FB:
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.facebookSuccess);
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
                    break;
                case UserLoginPresenter.LOGIN_GMAIL:
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.googleSuccess);
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
                    break;
            }
            String str = new Gson().toJson(userLoginEvent.userResponse);
            mOnUserLoginListener.onUserLoginSuccess(userLoginEvent.userResponse , str);
        }
    }

    @OnClick(R.id.iv_back)
    public void onBackPressed(){
        getActivity().onBackPressed();
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerHome);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.backSocial);
        MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.signUpSocial);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode & PermissionManager.ACCOUNTS_REQUEST)
                == PermissionManager.ACCOUNTS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GcmRegister.checkAndSetGcmId(getActivity(), null);
                onGoogleLoginClick();
            } else if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // TODO show message or something
            }
        }
    }
}






