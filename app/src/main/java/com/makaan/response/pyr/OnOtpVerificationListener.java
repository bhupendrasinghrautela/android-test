package com.makaan.response.pyr;


import com.makaan.response.ResponseError;

/**
 * Created by sunil on 30/12/15.
 */
public interface OnOtpVerificationListener {
    void OnOtpVerificationSuccess();
    void OnOtpVerificationFailed(ResponseError error);
}
