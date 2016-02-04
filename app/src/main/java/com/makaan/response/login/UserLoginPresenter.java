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

    public void showLoginChooserFragment(){
        mLoginSocialFragment = new LoginSocialFragment();
        mLoginSocialFragment.bindView(this, mOnUserLoginListener);
        mReplaceFragment.replaceFragment(mLoginSocialFragment, true);
    }

    public void showLoginWithMakaanFragment(){
        mLoginFragment = new LoginFragment();
        mLoginFragment.bindView(mOnUserLoginListener, this);
        mReplaceFragment.replaceFragment(mLoginFragment, true);
    }

    public void showSignUpFragment(){
        mSignUpFragment = new SignUpFragment();
        mSignUpFragment.bindView(mRegistrationListener);
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
