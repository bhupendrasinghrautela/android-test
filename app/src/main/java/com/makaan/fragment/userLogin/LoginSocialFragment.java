package com.makaan.fragment.userLogin;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
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
import com.makaan.response.login.OnLoginWithMakaanSelectedListener;
import com.makaan.response.login.OnUserLoginListener;

import com.makaan.response.login.UserLoginPresenter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.FacebookTokenInteractor;
import com.makaan.service.user.GoogleTokenInteractor;
import com.makaan.service.user.OnFacebookTokenListener;
import com.makaan.service.user.OnGoogleTokenListener;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.NetworkUtil;
import com.makaan.util.PermissionManager;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
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

        FacebookSdk.sdkInitialize(getActivity());
        mFacebookTokenInteractor = new FacebookTokenInteractor(getActivity(), this);
        mFacebookTokenInteractor.initFacebookSdk(savedInstanceState);
        parseLoginType(mLoginType);
    }

    public void bindView(OnLoginWithMakaanSelectedListener listener, OnUserLoginListener onUserLoginListener, int loginType){
        mOnLoginWithMakaanSelectedListener = listener;
        mOnUserLoginListener = onUserLoginListener;
        mLoginType = loginType;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_ACCOUNT_PICKER_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                GoogleTokenInteractor interactor = new GoogleTokenInteractor(getActivity(), LoginSocialFragment.this);
                interactor.requestGoogleAccessToken(accountName);
            }else{
                //TODO
            }
        }
    }

    @OnClick(R.id.fb_login)
    public void onFacebookLoginClick(){
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            return;
        }
        loginType=UserLoginPresenter.LOGIN_FB;

        if(AccessToken.getCurrentAccessToken()==null) {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email"));
        }else {
            ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithFacebookAccount(AccessToken.getCurrentAccessToken().getToken());
        }
    }

    @OnClick(R.id.gmail_login)
    public void onGoogleLoginClick(){
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            return;
        }
        if(PermissionManager.isPermissionRequestRequired(getActivity(), Manifest.permission.GET_ACCOUNTS)) {
            PermissionManager.begin().addRequest(PermissionManager.ACCOUNTS_REQUEST).request(getActivity());
        } else {
            loginType = UserLoginPresenter.LOGIN_GMAIL;
            try {
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]
                        {GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);

                startActivityForResult(intent, GOOGLE_ACCOUNT_PICKER_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.makaan_login)
    public void onMakaanLoginClick(){
        mOnLoginWithMakaanSelectedListener.onLoginWithMakaanSelected();

    }

    @Override
    public void onGoogleTokenSuccess(String token) {
        ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
        ))).loginWithGoogleAccount(token);
    }

    @Override
    public void onGoogleTokenFail() {
       // Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFacebookTokenSuccess(String token) {
        ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
        ))).loginWithFacebookAccount(token);
    }

    @Override
    public void onFacebookLoginManagerCallback() {

    }

    @Override
    public void onFacebookTokenFail() {
        //Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
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






