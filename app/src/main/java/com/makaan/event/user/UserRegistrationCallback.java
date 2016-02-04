package com.makaan.event.user;

import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.user.UserResponse;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by proptiger on 1/2/16.
 */
public class UserRegistrationCallback extends StringRequestCallback{
    @Override
    public void onSuccess(String response) {
        UserRegistrationEvent userRegistrationEvent = new UserRegistrationEvent();
        UserResponse userResponse =
                (UserResponse) JsonParser.parseJson(
                        response, UserResponse.class);

        if(userResponse == null || userResponse.getData() == null ) {

            ResponseError error = new ResponseError();
            error.setMsg("Data null");
            userRegistrationEvent.error = error;

        }else {
            //TODO populate UI field for project id

            userRegistrationEvent.userResponse = userResponse;
        }

        AppBus.getInstance().post(userRegistrationEvent);
    }

    @Override
    protected void onError() {
        super.onError();
        UserRegistrationEvent userRegistrationEvent = new UserRegistrationEvent();
        ResponseError error = new ResponseError();
        error.setMsg("volley error");
        userRegistrationEvent.error = error;
        AppBus.getInstance().post(userRegistrationEvent);
    }
}
