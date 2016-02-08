package com.makaan.event.user;

import com.makaan.cache.MasterDataCache;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.search.SearchResponse;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.response.user.UserResponse;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by sunil on 25/01/16.
 */
public class UserLoginCallback extends StringRequestCallback {
    @Override
    public void onSuccess(String response) {
        UserLoginEvent userLoginEvent = new UserLoginEvent();
        UserResponse userResponse =
                (UserResponse) JsonParser.parseJson(
                        response, UserResponse.class);

        if(userResponse == null || userResponse.getData() == null ) {

            ResponseError error = new ResponseError();
            error.msg = "There seems to be some error, please try later.";
            userLoginEvent.error = error;

        }else {
            //TODO populate UI field for project id

            userLoginEvent.userResponse = userResponse;
        }

        AppBus.getInstance().post(userLoginEvent);
    }

    @Override
    public void onError(ResponseError error) {
        UserLoginEvent userLoginEvent = new UserLoginEvent();
        userLoginEvent.error = error;
        AppBus.getInstance().post(userLoginEvent);
    }
}
