package com.makaan.event.user;

import android.util.Log;
import android.widget.Toast;

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
        if(baseResponse.getStatusCode().equals("2XX")){
            ResponseError error = new ResponseError();
            error.setMsg("successful");
            baseResponse.setError(error);
        }
        else{
            ResponseError error = new ResponseError();
            error.setMsg("error");
            baseResponse.setError(error);
        }
        AppBus.getInstance().post(baseResponse);
    }

    @Override
    protected void onError() {
        super.onError();
        BaseResponse baseResponse= new BaseResponse();
        ResponseError error = new ResponseError();
        error.setMsg("error");
        baseResponse.setError(error);
        AppBus.getInstance().post(baseResponse);
    }
}
