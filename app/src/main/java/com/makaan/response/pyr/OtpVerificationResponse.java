package com.makaan.response.pyr;

import com.makaan.response.BaseResponse;

/**
 * Created by proptiger on 22/1/16.
 */
public class OtpVerificationResponse extends BaseResponse{
    OtpData data;
    public OtpData getData() {
        return data;
    }

    public void setData(OtpData data) {
        this.data = data;
    }

    public class OtpData{
        boolean otpValidationSuccess;
        public boolean isOtpValidationSuccess() {
            return otpValidationSuccess;
        }

        public void setOtpValidationSuccess(boolean otpValidationSuccess) {
            this.otpValidationSuccess = otpValidationSuccess;
        }

    }
}
