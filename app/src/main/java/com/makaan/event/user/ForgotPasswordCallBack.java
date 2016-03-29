package com.makaan.event.user;

import com.makaan.network.StringRequestCallback;
import com.makaan.response.BaseResponse;
import com.makaan.response.ResponseError;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by proptiger on 2/2/16.
 */
public class ForgotPasswordCallBack extends StringRequestCallback {
    @Override
    public void onSuccess(String response) {
        //Log.e(TAG, response);
        BaseResponse baseResponse= (BaseResponse) JsonParser.parseJson(response, BaseResponse.class);
        AppBus.getInstance().post(baseResponse);
    }

    @Override
    public void onError(ResponseError error) {
        BaseResponse baseResponse= new BaseResponse();
        baseResponse.setError(error);
        AppBus.getInstance().post(baseResponse);
    }
}
