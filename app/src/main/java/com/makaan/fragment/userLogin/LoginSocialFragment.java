package com.makaan.fragment.userLogin;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
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
import com.makaan.R;
import com.makaan.response.login.OnLoginWithMakaanSelectedListener;
import com.makaan.response.login.OnUserLoginListener;

import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.FacebookTokenInteractor;
import com.makaan.service.user.GoogleTokenInteractor;
import com.makaan.service.user.OnFacebookTokenListener;
import com.makaan.service.user.OnGoogleTokenListener;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.NetworkUtil;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 29/12/15.
 */
public class LoginSocialFragment extends Fragment implements OnGoogleTokenListener,OnFacebookTokenListener {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_social,container,false);
        ButterKnife.bind(this, view);

        FacebookSdk.sdkInitialize(getActivity());
        mFacebookTokenInteractor = new FacebookTokenInteractor(getActivity(), this);
        mFacebookTokenInteractor.initFacebookSdk(savedInstanceState);

        return view;
    }

    public void bindView(OnLoginWithMakaanSelectedListener listener, OnUserLoginListener onUserLoginListener){
        mOnLoginWithMakaanSelectedListener = listener;
        mOnUserLoginListener = onUserLoginListener;
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

        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]
                    {GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);

            startActivityForResult(intent, GOOGLE_ACCOUNT_PICKER_CODE);
        } catch (Exception e) {}
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
        Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
    }
}






