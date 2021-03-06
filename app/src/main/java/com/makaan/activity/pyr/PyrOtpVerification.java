package com.makaan.activity.pyr;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.OtpVerificationService;
import com.makaan.service.PyrService;
import com.makaan.util.AppBus;
import com.makaan.util.SmsReceiver;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 7/1/16.
 */
public class PyrOtpVerification extends Fragment implements  SmsReceiver.OnVerificationListener, View.OnFocusChangeListener, View.OnKeyListener {

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

        mEditTextFirstDigit.setOnFocusChangeListener(this);
        mEditTextSecondDigit.setOnFocusChangeListener(this);
        mEditTextThirdDigit.setOnFocusChangeListener(this);
        mEditTextFourthDigit.setOnFocusChangeListener(this);

        mEditTextFirstDigit.setOnKeyListener(this);
        mEditTextSecondDigit.setOnKeyListener(this);
        mEditTextThirdDigit.setOnKeyListener(this);
        mEditTextFourthDigit.setOnKeyListener(this);
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
        }

        @Override
        public void afterTextChanged(Editable s) {
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

                Properties properties1 = MakaanEventPayload.beginBatch();
                properties1.put(MakaanEventPayload.CATEGORY, mPyrPagePresenter.getCategoryForPyrSubmit(mPyrPagePresenter.getSourceScreenName()));
                properties1.put(MakaanEventPayload.LABEL, mPyrPagePresenter.getLabelStringOnNextClick(mPyrPagePresenter.getPyrRequestObject()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.pyrOtpSubmit);
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

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b) {
            switch (view.getId()) {
                case R.id.et_first_digit: {

                    break;
                }
                case R.id.et_second_digit: {
                    if("".equalsIgnoreCase(mEditTextFirstDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextFirstDigit.requestFocus();
                            }
                        }, 500);
                    }
                    break;
                }
                case R.id.et_third_digit: {

                    if("".equalsIgnoreCase(mEditTextFirstDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextFirstDigit.requestFocus();
                            }
                        }, 500);
                    } else if("".equalsIgnoreCase(mEditTextSecondDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextSecondDigit.requestFocus();
                            }
                        }, 500);
                    }
                    break;
                }
                case R.id.et_fourth_digit: {


                    if("".equalsIgnoreCase(mEditTextFirstDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextFirstDigit.requestFocus();
                            }
                        }, 500);
                    } else if("".equalsIgnoreCase(mEditTextSecondDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextSecondDigit.requestFocus();
                            }
                        }, 500);
                    } else if("".equalsIgnoreCase(mEditTextThirdDigit.getText().toString())) {
                        mEditTextSecondDigit.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mEditTextThirdDigit.requestFocus();
                            }
                        }, 500);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL
                && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.et_second_digit: {
                    if("".equalsIgnoreCase(mEditTextSecondDigit.getText().toString())) {
                        mEditTextFirstDigit.onKeyDown(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextFirstDigit.onKeyUp(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextFirstDigit.requestFocus();
                        return true;
                    }
                    break;
                }
                case R.id.et_third_digit: {

                    if("".equalsIgnoreCase(mEditTextThirdDigit.getText().toString())) {
                        mEditTextSecondDigit.onKeyDown(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextSecondDigit.onKeyUp(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextSecondDigit.requestFocus();
                        return true;
                    }
                    break;
                }
                case R.id.et_fourth_digit: {


                    if("".equalsIgnoreCase(mEditTextFourthDigit.getText().toString())) {
                        mEditTextThirdDigit.onKeyDown(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextThirdDigit.onKeyUp(KeyEvent.KEYCODE_DEL, keyEvent);
                        mEditTextThirdDigit.requestFocus();
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
}
