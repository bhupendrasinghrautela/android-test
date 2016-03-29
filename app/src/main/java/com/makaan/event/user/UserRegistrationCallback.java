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
            error.msg = "There seems to be some error, please try later.";
            userRegistrationEvent.error = error;

        }else {
            //TODO populate UI field for project id

            userRegistrationEvent.userResponse = userResponse;
        }

        AppBus.getInstance().post(userRegistrationEvent);
    }


    @Override
    public void onError(ResponseError error) {
        UserRegistrationEvent userRegistrationEvent = new UserRegistrationEvent();
        userRegistrationEvent.error = error;
        AppBus.getInstance().post(userRegistrationEvent);
    }
}
