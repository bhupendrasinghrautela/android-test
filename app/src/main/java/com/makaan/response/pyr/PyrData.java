package com.makaan.response.pyr;

/**
 * Created by proptiger on 21/1/16.
 */
public class PyrData {
    boolean isOtpVerified;
    Long userId;
    Long enquiryIds[];

    public Long[] getEnquiryIds() {
        return enquiryIds;
    }

    public void setEnquiryIds(Long[] enquiryIds) {
        this.enquiryIds = enquiryIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isOtpVerified() {
        return isOtpVerified;
    }

    public void setIsOtpVerified(boolean isOtpVerified) {
        this.isOtpVerified = isOtpVerified;
    }

}
