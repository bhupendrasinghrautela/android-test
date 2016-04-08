package com.makaan.fragment.userLogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.response.login.OnSignUpSelectedListener;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.user.UserLoginService;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by makaanuser on 29/12/15.
 */
public class LoginFragment extends Fragment implements TextWatcher {

    public  static String TAG=LoginFragment.class.getSimpleName();
    @Bind(R.id.et_email)
    EditText mEditTextEmail;

    @Bind(R.id.et_password)
    EditText mEditTextPassword;

    @Bind(R.id.btn_login)
    Button mButtonLogin;

    @Bind(R.id.cb_password_login)
    CheckBox mCheckboxPassword;

    @Bind(R.id.tv_signup)
    TextView mSignUptext;

    @Bind(R.id.til_login_email)
    TextInputLayout mTilEmail;

    @Bind(R.id.til_login_password)
    TextInputLayout mTilPassword;

    private OnUserLoginListener mOnUserLoginListener;
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

        String lastUserName = CookiePreferences.getUserName(getActivity());
        if(!TextUtils.isEmpty(lastUserName)){
            mEditTextEmail.setText(lastUserName);
        }

        mEditTextEmail.addTextChangedListener(this);
        mEditTextPassword.addTextChangedListener(this);
        return view;
    }

    public void bindView(OnUserLoginListener listener, OnSignUpSelectedListener onSignUpSelectedListener){
        mOnUserLoginListener = listener;
        mSignUpSelectedListener=onSignUpSelectedListener;
    }

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
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, getString(R.string.invalid_email));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorLogin);
            mTilEmail.setError(getString(R.string.invalid_email));

        }else if(TextUtils.isEmpty(pwd)) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, getString(R.string.invalid_password));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorLogin);
            mTilPassword.setError(getString(R.string.invalid_password));

        } else {
           ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithMakaanAccount(email,pwd);
            mOnUserLoginListener.onUserLoginBegin();

            //remember just the user name
            CookiePreferences.setUserName(getActivity(), email);
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

    @OnClick(R.id.tv_signup)
    public void onSignUp(){
        //startActivity(new Intent(getActivity(), UserRegistrationActivity.class));
       mSignUpSelectedListener.onSignUpWithMakaanSelected();
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent){
        if(!isVisible()) {
            return;
        }
        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.userLogin);
        if(userLoginEvent.error!=null){
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.loginWithEmailFail);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
            mOnUserLoginListener.onUserLoginError(userLoginEvent.error);
        }else {
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.loginWithEmailSuccess);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.login);
            String str = new Gson().toJson(userLoginEvent.userResponse);
            mOnUserLoginListener.onUserLoginSuccess(userLoginEvent.userResponse, str);
        }
    }

    @OnClick(R.id.iv_back)
    public void onBackPressed(){
        getActivity().onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditTextEmail.getText().length() > 0)
            mTilEmail.setErrorEnabled(false);
        else
            mTilEmail.setErrorEnabled(true);

        if (mEditTextPassword.getText().length() > 0)
            mTilPassword.setErrorEnabled(false);
        else
            mTilPassword.setErrorEnabled(true);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
