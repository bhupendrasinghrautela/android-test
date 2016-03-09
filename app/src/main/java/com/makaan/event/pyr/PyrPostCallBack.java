package com.makaan.event.pyr;

import android.util.Log;

import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

import org.json.JSONObject;

/**
 * Created by makaanuser on 9/1/16.
 */
public class PyrPostCallBack extends StringRequestCallback {

    @Override
    public void onSuccess(String response) {
        Log.e("Success pyr", response);
        PyrPostResponse pyrPostResponse = (PyrPostResponse)
                JsonParser.parseJson(response, PyrPostResponse.class);
        AppBus.getInstance().post(pyrPostResponse);

    }

    @Override
    public void onError(ResponseError error) {
        PyrPostResponse pyrPostResponse=new PyrPostResponse();
        pyrPostResponse.setError(error);
        AppBus.getInstance().post(pyrPostResponse);
        //TODO handle error
    }
}
