package com.makaan.response.login;

import com.makaan.response.ResponseError;
import com.makaan.response.user.UserResponse;

/**
 * Created by proptiger on 1/2/16.
 */
public interface OnUserRegistrationListener {
    void onUserRegistrationSuccess(UserResponse userResponse, String response);
    void onUserRegistrationError(ResponseError error);
    void onUserRegistrationBegin();
}
