package com.makaan.response.login;


import com.makaan.response.ResponseError;
import com.makaan.response.user.UserResponse;

/**
 * Created by sunil on 29/12/15.
 */
public interface OnUserLoginListener {
    void onUserLoginSuccess(UserResponse userResponse, String response);
    void onUserLoginError(ResponseError error);
    void onUserLoginBegin();
}
