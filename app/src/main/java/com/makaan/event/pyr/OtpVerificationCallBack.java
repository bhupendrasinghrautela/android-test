package com.makaan.event.pyr;

import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.pyr.OtpVerificationResponse;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonParser;

/**
 * Created by proptiger on 21/1/16.
 */
public class OtpVerificationCallBack extends StringRequestCallback {
    @Override
    public void onSuccess(String response) {
        CommonUtil.TLog("Success otp verifi", "Error parsing city Trend Data");
        OtpVerificationResponse otpVerificationResponse = (OtpVerificationResponse)
                JsonParser.parseJson(response, OtpVerificationResponse.class);
        AppBus.getInstance().post(otpVerificationResponse);
    }

    @Override
    public void onError(ResponseError error) {
        //TODO handle error
    }
}
