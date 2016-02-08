package com.makaan.event.lead;

import android.util.Log;

import com.makaan.network.StringRequestCallback;
import com.makaan.response.BaseResponse;
import com.makaan.response.ResponseError;
import com.makaan.response.leadForm.InstantCallbackResponse;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadInstantCallback extends StringRequestCallback {
    @Override
    public void onSuccess(String response) {
        InstantCallbackResponse instantCallResponse = (InstantCallbackResponse)
                JsonParser.parseJson(response, InstantCallbackResponse.class);
        AppBus.getInstance().post(instantCallResponse);
    }

    @Override
    public void onError(ResponseError error) {
        //TODO handle error here
    }
}
