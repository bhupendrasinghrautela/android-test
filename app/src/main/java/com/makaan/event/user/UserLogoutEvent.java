package com.makaan.event.user;

import com.makaan.response.BaseEvent;
import com.makaan.response.BaseResponse;
import com.makaan.response.user.UserResponse;

public class UserLogoutEvent extends BaseEvent {

    public boolean isLogoutSuccessfull;

    public boolean isLogoutSuccessfull() {
        return isLogoutSuccessfull;
    }

    public void setIsLogoutSuccessfull(boolean isLogoutSuccessfull) {
        this.isLogoutSuccessfull = isLogoutSuccessfull;
    }

}