package com.makaan.fragment.userLogin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.event.user.UserRegistrationEvent;
import com.makaan.response.login.OnUserRegistrationListener;
import com.makaan.response.login.UserRegistrationDto;
import com.makaan.service.user.UserRegistrationService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by proptiger on 29/1/16.
 */
public class SignUpFragment extends Fragment {

    public  static String TAG=SignUpFragment.class.getSimpleName();
    @Bind(R.id.sign_up_username)
    EditText mEditTextName;
    @Bind(R.id.sign_up_email)
    EditText mEditTextEmail;
    @Bind(R.id.sign_up_password)
    EditText mEditTextPassword;
    @Bind(R.id.sign_up_button)
    Button mButtonLogin;
    private OnUserRegistrationListener mOnUserRegistrationListener;
    private UserRegistrationDto userRegistrationDto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_sign_up,container,false);
        ButterKnife.bind(this, view);
        AppBus.getInstance().register(this);
        return view;
    }

    public void bindView(OnUserRegistrationListener listener){
        mOnUserRegistrationListener = listener;
    }


    @OnClick(R.id.sign_up_button)
    public void onLoginClick() {

        String email = mEditTextEmail.getText().toString().trim();
        String pwd = mEditTextPassword.getText().toString().trim();
        String name=mEditTextName.getText().toString().trim();
        if(!CommonUtil.isValidEmail(email)) {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(),getString(R.string.enter_password),Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(name)){
            Toast.makeText(getActivity(),"enter name",Toast.LENGTH_SHORT).show();
        }
        else {
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
        if(userRegistrationEvent.error!=null && (userRegistrationEvent.error.getMsg().equals("Data null")||
                userRegistrationEvent.error.getMsg().equalsIgnoreCase("volley error"))){
            mOnUserRegistrationListener.onUserRegistrationError(userRegistrationEvent.error);
        }
        else {
            String str = new Gson().toJson(userRegistrationEvent.userResponse);
            mOnUserRegistrationListener.onUserRegistrationSuccess(userRegistrationEvent.userResponse , str);
        }

    }

}
