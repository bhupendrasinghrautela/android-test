package com.makaan.response.login;

import android.content.Context;

import com.makaan.fragment.userLogin.LoginFragment;
import com.makaan.fragment.userLogin.LoginSocialFragment;
import com.makaan.fragment.userLogin.ReplaceFragment;
import com.makaan.fragment.userLogin.SignUpFragment;

/**
 * Created by sunil on 29/12/15.
 */
public class UserLoginPresenter implements
        OnLoginWithMakaanSelectedListener,OnSignUpSelectedListener {

    public static final String LOGIN_TYPE = "loginType";
    public static final int LOGIN_NONE = 0x00;
    public static final int LOGIN_FB = 0x01;
    public static final int LOGIN_GMAIL = 0x02;
    public static final int LOGIN_MAKAAN = 0x03;

    private OnUserLoginListener mOnUserLoginListener;
    private OnUserRegistrationListener mRegistrationListener;
    private LoginSocialFragment mLoginSocialFragment;
    private LoginFragment mLoginFragment;
    private SignUpFragment mSignUpFragment;
    private ReplaceFragment mReplaceFragment;
    private Context mContext;


    public UserLoginPresenter(Context context, ReplaceFragment replaceFragment,
                              OnUserLoginListener loginListener,OnUserRegistrationListener onUserRegistrationListener){
        mOnUserLoginListener = loginListener;
        mReplaceFragment = replaceFragment;
        mRegistrationListener=onUserRegistrationListener;
        mContext = context;
    }

    public void showLoginChooserFragment(int loginType){
        mLoginSocialFragment = new LoginSocialFragment();
        mLoginSocialFragment.bindView(this, mOnUserLoginListener, loginType);
        mReplaceFragment.replaceFragment(mLoginSocialFragment, false);
    }

    public void showLoginWithMakaanFragment(){
        mLoginFragment = new LoginFragment();
        mLoginFragment.bindView(mOnUserLoginListener, this);
        mReplaceFragment.replaceFragment(mLoginFragment, true);
    }

    public void showSignUpFragment(){
        mSignUpFragment = new SignUpFragment();
        mSignUpFragment.bindView(mRegistrationListener, mOnUserLoginListener);
        mReplaceFragment.replaceFragment(mSignUpFragment, true);
    }

    @Override
    public void onLoginWithMakaanSelected() {
        showLoginWithMakaanFragment();
    }

    @Override
    public void onSignUpWithMakaanSelected() {
        showSignUpFragment();
    }


}
