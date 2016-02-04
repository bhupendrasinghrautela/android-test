package com.makaan.fragment.userLogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.response.login.OnSignUpSelectedListener;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by makaanuser on 29/12/15.
 */
public class LoginFragment extends Fragment {

    public  static String TAG=LoginFragment.class.getSimpleName();
    /*@Bind(R.id.iv_back)
    ImageView mImageViewBack;*/
    @Bind(R.id.et_email)
    EditText mEditTextEmail;
    @Bind(R.id.et_password)
    EditText mEditTextPassword;
    @Bind(R.id.btn_login)
    Button mButtonLogin;
    @Bind(R.id.cb_password_login)
    CheckBox mCheckboxPassword;
    private OnUserLoginListener mOnUserLoginListener;
    @Bind(R.id.tv_signup)
    TextView mSignUptext;
    private OnSignUpSelectedListener mSignUpSelectedListener;
    boolean mBound = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_login,container,false);
        ButterKnife.bind(this, view);
        if(!mBound) {
            AppBus.getInstance().register(this);
            mBound=true;
        }
        mSignUptext=(TextView) view.findViewById(R.id.tv_signup);
        String text = "<font color=#000000>new to makaan?</font><font color=#e71c28> sign up!</font>";
        mSignUptext.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);

        return view;
    }

    public void bindView(OnUserLoginListener listener, OnSignUpSelectedListener onSignUpSelectedListener){
        mOnUserLoginListener = listener;
        mSignUpSelectedListener=onSignUpSelectedListener;
    }

    /*@OnClick(R.id.iv_back)
    public void backClicked() {

    }*/

    @OnClick(R.id.tv_forgot_pwd)
    void forgotClicked(){
        ForgotPasswordDialogFragment forgotPasswordDialogFragment = new ForgotPasswordDialogFragment();
        forgotPasswordDialogFragment.show(getFragmentManager(),TAG);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {

        String email = mEditTextEmail.getText().toString().trim();
        String pwd = mEditTextPassword.getText().toString().trim();
        if(!CommonUtil.isValidEmail(email)) {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(),getString(R.string.enter_password),Toast.LENGTH_SHORT).show();
        } else {
           ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithMakaanAccount(email,pwd);
            mOnUserLoginListener.onUserLoginBegin();
        }
    }

    @OnCheckedChanged(R.id.cb_password_login)
    public void onCheckedChanged( boolean isChecked) {
        // checkbox status is changed from uncheck to checked.
        if (!isChecked) {
            // show password
            mEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            // hide password
            mEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    @OnCheckedChanged(R.id.cb_remember_me)
    public void onRememberMeCheckedChanged( boolean isChecked) {
        // checkbox status is changed from uncheck to checked.
        if (!isChecked) {

        } else {

        }
    }

    @OnClick(R.id.tv_signup)
    public void onSignUp(){
        //startActivity(new Intent(getActivity(), UserRegistrationActivity.class));
       mSignUpSelectedListener.onSignUpWithMakaanSelected();
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent){
        if(userLoginEvent.error!=null && userLoginEvent.error.getMsg().equals("Data null")){
            mOnUserLoginListener.onUserLoginError(userLoginEvent.error);
        }
        else {
            String str = new Gson().toJson(userLoginEvent.userResponse);
            mOnUserLoginListener.onUserLoginSuccess(userLoginEvent.userResponse , str);
        }
    }
}