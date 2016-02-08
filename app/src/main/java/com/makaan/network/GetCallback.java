package com.makaan.network;

import com.makaan.response.ResponseError;

/**
 * Created by vaibhav on 29/12/15.
 */
public abstract class GetCallback {

    public abstract void onError(ResponseError error);
}
