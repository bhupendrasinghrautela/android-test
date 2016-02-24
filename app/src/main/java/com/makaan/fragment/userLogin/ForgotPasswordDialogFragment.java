package com.makaan.fragment.userLogin;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.makaan.R;
import com.makaan.response.BaseResponse;
import com.makaan.service.user.ForgotPasswordService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 30/12/15.
 */
public class ForgotPasswordDialogFragment extends DialogFragment {

    @Bind(R.id.iv_close)
    ImageView mImageViewClose;
    @Bind(R.id.et_email)
    EditText mEditTextemail;

    public ForgotPasswordDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_forgot_password,container);
        ButterKnife.bind(this, view);
        AppBus.getInstance().register(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @OnClick(R.id.iv_close)
    public void closeClicked()
    {
        dismiss();
    }

    @OnClick(R.id.btn_send_reset)
    public void sendResetClicked()
    {
        if(CommonUtil.isValidEmail(mEditTextemail.getText().toString())) {
            /*ForgotPasswordService mForgotPasswordService = new ForgotPasswordService();
            mForgotPasswordService.forgotPasswordRequest(mEditTextemail.getText().toString().trim());*/
            ((ForgotPasswordService) (MakaanServiceFactory.getInstance().getService(ForgotPasswordService.class
            ))).forgotPasswordRequest(mEditTextemail.getText().toString().trim());
        }
        else{
            Toast.makeText(getActivity(),getString(R.string.enter_valid_email),Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void forgotPasswordResponse(BaseResponse baseResponse){
        if(getActivity()==null || getActivity().isFinishing()){
            return;
        }
        if(baseResponse.getStatusCode()!=null && baseResponse.getStatusCode().equals("2XX")){
            Toast.makeText(getActivity(),getActivity().getString(R.string.password_recovery),Toast.LENGTH_SHORT).show();
        }else {
            if(baseResponse.getError() != null && !TextUtils.isEmpty(baseResponse.getError().msg)) {
                Toast.makeText(getActivity(), baseResponse.getError().msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
            }
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }
}
