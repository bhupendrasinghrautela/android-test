package com.makaan.fragment.userLogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.user.UserRegistrationEvent;
import com.makaan.response.login.OnUserLoginListener;
import com.makaan.response.login.OnUserRegistrationListener;
import com.makaan.response.login.UserRegistrationDto;
import com.makaan.service.user.UserLoginService;
import com.makaan.service.user.UserRegistrationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by proptiger on 29/1/16.
 */
public class SignUpFragment extends Fragment implements View.OnFocusChangeListener,TextWatcher {

    public  static String TAG=SignUpFragment.class.getSimpleName();
    @Bind(R.id.sign_up_username)
    EditText mEditTextName;
    @Bind(R.id.sign_up_email)
    EditText mEditTextEmail;
    @Bind(R.id.sign_up_password)
    EditText mEditTextPassword;
    @Bind(R.id.sign_up_button)
    Button mButtonLogin;
    @Bind(R.id.til_username)
    TextInputLayout mTilUsername;
    @Bind(R.id.til_signup_email)
    TextInputLayout mTilEmail;
    @Bind(R.id.til_signup_password)
    TextInputLayout mTilPassword;
    private boolean mAlreadyLoaded=false;
    private OnUserRegistrationListener mOnUserRegistrationListener;
    private UserRegistrationDto userRegistrationDto;
    private OnUserLoginListener mOnUserLoginListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_sign_up,container,false);
        ButterKnife.bind(this, view);
        AppBus.getInstance().register(this);
        mEditTextEmail.setOnFocusChangeListener(this);
        mEditTextName.setOnFocusChangeListener(this);
        mEditTextPassword.setOnFocusChangeListener(this);
        mEditTextPassword.addTextChangedListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!mAlreadyLoaded){
             /*--------------------track--------------code-------------*/
            Properties properties1 = MakaanEventPayload.beginBatch();
            properties1.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_LOGIN);
            properties1.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_LOGIN_SIGNUP);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.screenName);
            /*--------------------------------------------------------*/
            mAlreadyLoaded=true;
        }

    }

    public void bindView(OnUserRegistrationListener listener, OnUserLoginListener loginListener){
        mOnUserRegistrationListener = listener;
        mOnUserLoginListener = loginListener;
    }

    @OnClick(R.id.sign_up_button)
    public void onLoginClick() {

        String email = mEditTextEmail.getText().toString().trim();
        String pwd = mEditTextPassword.getText().toString().trim();
        String name=mEditTextName.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, getString(R.string.enter_username));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorSignUp);
            mTilUsername.setError(getString(R.string.enter_username));

            //Toast.makeText(getActivity(),getString(R.string.enter_username),Toast.LENGTH_SHORT).show();
        }else if(!CommonUtil.isValidEmail(email)) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, getString(R.string.enter_valid_email));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorSignUp);
            mTilEmail.setError(getString(R.string.enter_valid_email));

            //Toast.makeText(getActivity(), getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pwd)) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, getString(R.string.enter_password));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorSignUp);

            mTilPassword.setError(getString(R.string.enter_password));
            //Toast.makeText(getActivity(),getString(R.string.enter_password),Toast.LENGTH_SHORT).show();
        }else{
            userRegistrationDto=new UserRegistrationDto();
            userRegistrationDto.setFullName(mEditTextName.getText().toString().trim());
            userRegistrationDto.setEmail(mEditTextEmail.getText().toString().trim());
            userRegistrationDto.setPassword(mEditTextPassword.getText().toString().trim());
            userRegistrationDto.setConfirmPassword(mEditTextPassword.getText().toString().trim());
            userRegistrationDto.setCountryId("1");
            userRegistrationDto.setDomainId(1);
            /*UserRegistrationService userRegistrationService =new UserRegistrationService();
            userRegistrationService.registerUser(userRegistrationDto);*/
            ((UserRegistrationService) (MakaanServiceFactory.getInstance().getService(UserRegistrationService.class
            ))).registerUser(userRegistrationDto);
            mOnUserRegistrationListener.onUserRegistrationBegin();
        }
    }

    @OnCheckedChanged(R.id.sign_up_password_checkbox)
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

    @Subscribe
    public void registrationResults(UserRegistrationEvent userRegistrationEvent){
        if(!isVisible()) {
            return;
        }
        if(userRegistrationEvent.error!=null ){
            mOnUserRegistrationListener.onUserRegistrationError(userRegistrationEvent.error);
        }
        else {
            String str = new Gson().toJson(userRegistrationEvent.userResponse);
            mOnUserRegistrationListener.onUserRegistrationSuccess(userRegistrationEvent.userResponse , str);

            ((UserLoginService) (MakaanServiceFactory.getInstance().getService(UserLoginService.class
            ))).loginWithMakaanAccount(userRegistrationDto.getEmail(), userRegistrationDto.getPassword());
        }

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
        getActivity().finish(); 
    }


    @Override
    public void onFocusChange(View view, boolean focusGain) {
        EditText editText=(EditText)view;
        if(null!=editText) {
            switch (editText.getId()){
                case R.id.sign_up_username:{
                    if(!focusGain && TextUtils.isEmpty(mEditTextName.getText().toString().trim())){
                        mTilUsername.setError(getString(R.string.enter_username));
                    }else {
                        mTilUsername.setErrorEnabled(false);
                    }
                    break;
                }
                case R.id.sign_up_email:{
                    if(!focusGain && !CommonUtil.isValidEmail(mEditTextEmail.getText().toString().trim())){
                        mTilEmail.setError(getString(R.string.enter_valid_email));
                    }else {
                        mTilEmail.setErrorEnabled(false);
                    }

                    break;
                }
                case R.id.sign_up_password:{
                        if(!focusGain &&  TextUtils.isEmpty(mEditTextPassword.getText().toString().trim())){
                            mTilPassword.setError(getString(R.string.enter_password));
                        }
                    break;
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mEditTextPassword.getText().length() > 0)
            mTilPassword.setErrorEnabled(false);
        else
            mTilPassword.setErrorEnabled(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
