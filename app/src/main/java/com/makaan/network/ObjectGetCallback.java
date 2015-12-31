package com.makaan.network;

import org.json.JSONObject;

/**
 * Created by vaibhav on 29/12/15.
 */
public abstract class ObjectGetCallback extends GetCallback {

    public abstract void onSuccess(Object responseObject);
}
