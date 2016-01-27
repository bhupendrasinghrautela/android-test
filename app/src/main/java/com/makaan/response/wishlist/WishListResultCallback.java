package com.makaan.response.wishlist;

import android.util.Log;

import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListResultCallback extends StringRequestCallback {
    @Override
    public void onSuccess(String response) {

        //TODO add missing display fields in the response

        WishListResultEvent wishListResultEvent = new WishListResultEvent();
        WishListResponse wishListResponse =
                (WishListResponse) JsonParser.parseJson(
                        response, WishListResponse.class);

        if(wishListResponse == null || wishListResponse.data == null ) {

            ResponseError error = new ResponseError();
            error.setMsg("Data null");
            wishListResultEvent.error = error;

        }else {

            wishListResultEvent.wishListResponse = wishListResponse;
        }

        AppBus.getInstance().post(wishListResultEvent);

    }
}
