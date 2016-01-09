package com.makaan.event.pyr;

import com.makaan.network.JSONGetCallback;
import com.makaan.util.AppBus;

import org.json.JSONObject;

/**
 * Created by vaibhav on 09/01/16.
 */
public class TopAgentLocalityGetCallback extends JSONGetCallback {

    @Override
    public void onSuccess(JSONObject responseObject) {
        //parse response  = responseObj and raise event

        //AppBus.getInstance().post(new TopAgentLocalityGetEvent());
    }
}
