package com.makaan.network;

import org.json.JSONObject;

/**
 * Created by vaibhav on 23/12/15.
 */
public abstract class JSONGetCallback extends GetCallback {

    public abstract void onSuccess(JSONObject responseObject);


}
