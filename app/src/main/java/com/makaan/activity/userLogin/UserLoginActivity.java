package com.makaan.activity.userLogin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.fragment.userLogin.ReplaceFragment;
import com.makaan.network.VolleyErrorParser;
import com.makaan.response.ResponseError;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.response.login.OnUserRegistrationListener;
import com.makaan.response.login.UserLoginPresenter;

import com.makaan.response.user.UserResponse;
import com.makaan.ui.CommonProgressDialog;
import com.makaan.util.LoginPreferences;

/**
 * Created by sunil on 29/12/15.
 */
public class UserLoginActivity extends AppCompatActivity implements ReplaceFragment, OnUserLoginListener,OnUserRegistrationListener {

    private FragmentTransaction mFragmentTransaction;
    private UserLoginPresenter mUserLoginPresenter;
    private CommonProgressDialog mProgressDialog;
    public static final String PLEASE_WAIT="Please wait...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_activity);
        mProgressDialog =new CommonProgressDialog();
        mUserLoginPresenter = new UserLoginPresenter(this, this, this, this);
        mUserLoginPresenter.showLoginChooserFragment();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void onUserLoginSuccess(UserResponse userResponse, String response) {
        if(mProgressDialog !=null)
            mProgressDialog.dismissDialog();
        LoginPreferences.setUserInfo(this, response);
        LoginPreferences.setUserLoggedIn(this);
        Toast.makeText(this, "Welcome " +
                userResponse.getData().firstName, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onUserLoginError(ResponseError error) {
        if(mProgressDialog !=null)
        mProgressDialog.dismissDialog();
        Toast.makeText(this,error.msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserLoginBegin() {
            mProgressDialog.showDialog(this, PLEASE_WAIT);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            super.onBackPressed();
        }
    }

    @Override
    public void onUserRegistrationSuccess(UserResponse userResponse, String response) {
        if(mProgressDialog !=null)
            mProgressDialog.dismissDialog();
        Toast.makeText(this, "Registration Successful ", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUserRegistrationError(ResponseError error) {
        if(mProgressDialog !=null)
            mProgressDialog.dismissDialog();
        Toast.makeText(this,error.msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserRegistrationBegin() {
        mProgressDialog.showDialog(this, PLEASE_WAIT);
    }
}
