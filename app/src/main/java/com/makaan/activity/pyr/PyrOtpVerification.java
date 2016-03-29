package com.makaan.activity.pyr;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.lead.LeadFormPresenter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.pyr.OnOtpVerificationListener;
import com.makaan.response.pyr.OtpVerificationResponse;
import com.makaan.response.pyr.PyrData;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.OtpVerificationService;
import com.makaan.service.PyrService;
import com.makaan.service.SearchService;
import com.makaan.util.AppBus;
import com.makaan.util.SmsReceiver;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 7/1/16.
 */
public class PyrOtpVerification extends Fragment implements  SmsReceiver.OnVerificationListener{

    @Bind(R.id.et_first_digit)
    EditText mEditTextFirstDigit;
    @Bind(R.id.et_second_digit)
    EditText mEditTextSecondDigit;
    @Bind(R.id.et_third_digit)
    EditText mEditTextThirdDigit;
    @Bind(R.id.et_fourth_digit)
    EditText mEditTextFourthDigit;
    @Bind(R.id.iv_verify)
    ImageView mImageViewVerify;
    @Bind(R.id.tv_resend)
    TextView mTextViewResend;
    @Bind(R.id.otp_screen_skip)
    TextView mOtpScreenSkip;
    @Bind(R.id.pyr_user_email_id)
    TextView mUserName;
    @Bind(R.id.pyr_user_phone_no)
    TextView mPhoneNo;
    @OnClick(R.id.tv_resend)
    void sendOtp(){
        //TODO make call to get the otp
    }

    private SmsReceiver mSmsReceiver;
    private PyrPagePresenter mPyrPagePresenter ;
    private OnOtpVerificationListener mOnOtpVerificationListener;
    private String mUserId;
    private String mOtp;
    private PyrData pyrData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        ButterKnife.bind(this, view);
        mEditTextFirstDigit.addTextChangedListener(firstDigitWatcher);
        mEditTextSecondDigit.addTextChangedListener(secondDigitWatcher);
        mEditTextThirdDigit.addTextChangedListener(thirdDigitWatcher);
        mEditTextFourthDigit.addTextChangedListener(fourthDigitWatcher);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppBus.getInstance().register(this);
        if(getActivity() instanceof PyrPageActivity) {
            mPyrPagePresenter= PyrPagePresenter.getPyrPagePresenter();
            mUserName.setText(mPyrPagePresenter.getName() != null ? mPyrPagePresenter.getName() : "");
            mPhoneNo.setText(mPyrPagePresenter.getPhonNumber() != null ? mPyrPagePresenter.getPhonNumber() : "");
        }
        else if(getActivity() instanceof LeadFormActivity) {
            mUserName.setText(LeadFormPresenter.getLeadFormPresenter().getName() != null ? LeadFormPresenter.getLeadFormPresenter().getName() : "");
            mPhoneNo.setText(LeadFormPresenter.getLeadFormPresenter().getPhone() != null ? LeadFormPresenter.getLeadFormPresenter().getPhone() : "");
        }
        mSmsReceiver = new SmsReceiver();
        mSmsReceiver.setOnVerificationListener(this);
        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(mSmsReceiver, smsFilter);
        PyrService pyrService = (PyrService) MakaanServiceFactory.getInstance().getService(PyrService.class);
        CountDownTimer countDownTimer=new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                mOtpScreenSkip.setClickable(true);
                mOtpScreenSkip.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mSmsReceiver !=null) {
            getActivity().unregisterReceiver(mSmsReceiver);
            mSmsReceiver = null;
        }
        AppBus.getInstance().unregister(this);
    }

    public void bindView(OnOtpVerificationListener listener, String uid, String otp){
        mOnOtpVerificationListener = listener;
        mUserId = uid;
        mOtp = otp;
    }

    /* @OnClick(R.id.iv_back)
     public void backClicked()
     {

     }
 */
    public void setData(PyrData pyrData){
        this.pyrData=pyrData;
    }

    @OnClick(R.id.iv_verify)
    public void verifyClicked(){
        if(mEditTextFirstDigit.getText().toString().trim().length()==1
                && mEditTextSecondDigit.getText().toString().trim().length()==1
                && mEditTextThirdDigit.getText().toString().trim().length()==1
                &&mEditTextFourthDigit.getText().toString().trim().length()==1){
            mOtp=mEditTextFirstDigit.getText().toString()+mEditTextSecondDigit.getText().toString()+
                    mEditTextThirdDigit.getText().toString()+mEditTextFourthDigit.getText().toString();
        }

        PyrRequest pyrRequest=null;
        if(getActivity() instanceof PyrPageActivity) {
            pyrRequest = mPyrPagePresenter.getPyrRequestObject();
        }
        else if(getActivity() instanceof LeadFormActivity) {
            pyrRequest = LeadFormPresenter.getLeadFormPresenter().getPyrRequest();
        }
        if(pyrRequest!=null && pyrData!=null && mOtp!=null ) {
            if(mOtp.length()<4) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
            }else {
                ((OtpVerificationService) (MakaanServiceFactory.getInstance().getService(OtpVerificationService.class))).makeOtpVerificationRequest(
                        pyrData.getEnquiryIds()[0], mOtp, pyrRequest.getPhone(), String.valueOf(pyrData.getUserId()));
            }
            }
    }

    @OnClick(R.id.tv_resend)
    public void resendClicked()
    {
        PyrRequest pyrRequest=null;
        if(getActivity() instanceof PyrPageActivity) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.resend);
            MakaanEventPayload.endBatch(getContext(), mPyrPagePresenter.getOtpAction(mPyrPagePresenter.getSourceScreenName()));
            pyrRequest = mPyrPagePresenter.getPyrRequestObject();
        }
        else if(getActivity() instanceof LeadFormActivity) {
            pyrRequest = LeadFormPresenter.getLeadFormPresenter().getPyrRequest();
        }
        if(pyrRequest!=null && pyrData!=null) {
            ((OtpVerificationService) (MakaanServiceFactory.getInstance().getService(OtpVerificationService.class))).makeOtpResendRequest(
                    pyrData.getEnquiryIds()[0], pyrRequest.getPhone(), String.valueOf(pyrData.getUserId()));
        }
    }

    @OnClick(R.id.otp_screen_skip)
    public void skip(){
        if(getActivity() instanceof PyrPageActivity) {
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.skip);
            MakaanEventPayload.endBatch(getContext(), mPyrPagePresenter.getOtpAction(mPyrPagePresenter.getSourceScreenName()));
        }
        if (mPyrPagePresenter!=null && mPyrPagePresenter.isMakkanAssist()) {
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.showThankYouScreenFragment(true, true, false);
        }
        else if(mPyrPagePresenter!=null){
            PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
            mPyrPagePresenter.showThankYouScreenFragment(false, true, false);
        }else {
            if (LeadFormPresenter.getLeadFormPresenter().isAssist()) {
                LeadFormPresenter.getLeadFormPresenter().showThankYouScreenFragment(true, false,3);
            } else {
                LeadFormPresenter.getLeadFormPresenter().showThankYouScreenFragment(false, false,3);
            }
        }
    }

    private final TextWatcher firstDigitWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.toString().length()>0)
            {
                mEditTextSecondDigit.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private final TextWatcher secondDigitWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.toString().length()>0)
            {
                mEditTextThirdDigit.requestFocus();
            }else {
                mEditTextFirstDigit.requestFocus();
            }
            /*set focus to first editext*/
            if (mEditTextFirstDigit.getText().length() == 0) {
                mEditTextFirstDigit.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private final TextWatcher thirdDigitWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()>0)
            {
                mEditTextFourthDigit.requestFocus();
            }else{
                mEditTextSecondDigit.requestFocus();
            }
            /*set focus to first editext*/
            if (mEditTextFirstDigit.getText().length() == 0) {
                mEditTextFirstDigit.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher fourthDigitWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0)
            {
                mEditTextThirdDigit.requestFocus();
            }
            /*set focus to first editext*/
            if (mEditTextFirstDigit.getText().length() == 0) {
                mEditTextFirstDigit.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()==0)
            {
                mEditTextThirdDigit.requestFocus();
            }
        }
    };

    @Override
    public void getOTP(String OTP) {
        if(!TextUtils.isEmpty(OTP)) {
            mEditTextFirstDigit.setText(String.valueOf(OTP.charAt(0)));
            mEditTextSecondDigit.setText(String.valueOf(OTP.charAt(1)));
            mEditTextThirdDigit.setText(String.valueOf(OTP.charAt(2)));
            mEditTextFourthDigit.setText(String.valueOf(OTP.charAt(3)));
            mOtp=OTP;
        }
    }

    @Subscribe
    public void otpResponse(OtpVerificationResponse verificationResponse){
        if(verificationResponse.getData().isOtpValidationSuccess()) {
            if(getActivity() instanceof PyrPageActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.success);
                MakaanEventPayload.endBatch(getContext(), mPyrPagePresenter.getOtpAction(mPyrPagePresenter.getSourceScreenName()));
            }

            if ((mPyrPagePresenter!=null && mPyrPagePresenter.isMakkanAssist())) {
                PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
                mPyrPagePresenter.showThankYouScreenFragment(true, true, false);
            }
            else if(mPyrPagePresenter!=null){
                PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
                mPyrPagePresenter.showThankYouScreenFragment(false, true, false);
            }else {
                if (LeadFormPresenter.getLeadFormPresenter().isAssist()) {
                    LeadFormPresenter.getLeadFormPresenter().showThankYouScreenFragment(true, false,3);
                } else {
                    LeadFormPresenter.getLeadFormPresenter().showThankYouScreenFragment(false, false,3);
                }
            }
        }
        else {
            if(getActivity() instanceof PyrPageActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.incorrectOtp);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorPyr);
            }
        }
    }

}
