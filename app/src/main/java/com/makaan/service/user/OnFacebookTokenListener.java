package com.makaan.service.user;

/**
 * Created by sunil on 30/12/15.
 */
public interface OnFacebookTokenListener {
    void onFacebookTokenSuccess(String token);
    void onFacebookTokenFail(Exception exception);
    void onFacebookCancel();
}
