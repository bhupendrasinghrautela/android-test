package com.makaan.service.user;

/**
 * Created by sunil on 30/12/15.
 */
public interface OnGoogleTokenListener {
    void onGoogleTokenSuccess(String token);
    void onGoogleTokenFail();
}
