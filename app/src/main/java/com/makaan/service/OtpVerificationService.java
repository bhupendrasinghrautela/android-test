package com.makaan.service;

import com.crashlytics.android.Crashlytics;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.constants.ApiConstants;
import com.makaan.event.pyr.OtpVerificationCallBack;
import com.makaan.network.MakaanNetworkClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by proptiger on 21/1/16.
 */
public class OtpVerificationService implements MakaanService {
    public final String TAG=PyrOtpVerification.class.getSimpleName();

    public void makeOtpVerificationRequest(Long enquiryId, String otpValue ,String phone, String userId){
        StringBuilder otpVerificationUrl = new StringBuilder(ApiConstants.PYR);
        JSONObject jsonObject =null;

        if(enquiryId!=null){
            otpVerificationUrl.append("/"+enquiryId);
        }

        if(otpValue!=null && !otpValue.isEmpty()){
            otpVerificationUrl.append("?otp=");
            otpVerificationUrl.append(otpValue);
        }

        if(phone!=null && userId!=null && !phone.isEmpty() && !userId.isEmpty()){
            try {
                jsonObject=new JSONObject();
                jsonObject.put("phone", phone);
                jsonObject.put("userId", userId);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }

        if(jsonObject!=null){
            MakaanNetworkClient.getInstance().put(otpVerificationUrl.toString(), jsonObject, new OtpVerificationCallBack(),TAG);
        }
    }

    public void makeOtpResendRequest(Long enquiryId,String phone, String userId){
        StringBuilder otpVerificationUrl = new StringBuilder(ApiConstants.PYR);
        JSONObject jsonObject =null;

        if(enquiryId!=null){
            otpVerificationUrl.append("/"+enquiryId);
        }

        if(phone!=null && userId!=null && !phone.isEmpty() && !userId.isEmpty()){
            try {
                jsonObject=new JSONObject();
                jsonObject.put("phoneToVerify", phone);
                jsonObject.put("userId", userId);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }

        if(jsonObject!=null){
            MakaanNetworkClient.getInstance().put(otpVerificationUrl.toString(), jsonObject, new OtpVerificationCallBack(),TAG);
        }
    }
}
