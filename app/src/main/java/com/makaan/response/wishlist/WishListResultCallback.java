package com.makaan.response.wishlist;

import android.util.Log;

import com.makaan.cache.MasterDataCache;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListResultCallback extends StringRequestCallback {
    private WishListResponseUICallback wishListResponseUICallback;

    public WishListResultCallback(){}

    public WishListResultCallback(WishListResponseUICallback wishListResponseUICallback){
        this.wishListResponseUICallback = wishListResponseUICallback;
    }

    @Override
    public void onSuccess(String response) {

        //TODO add missing display fields in the response

        WishListResultEvent wishListResultEvent = new WishListResultEvent();
        WishListResponse wishListResponse =
                (WishListResponse) JsonParser.parseJson(
                        response, WishListResponse.class);

        if(wishListResponse == null || wishListResponse.data == null ) {

            ResponseError error = new ResponseError();
            error.msg = "No data";
            wishListResultEvent.error = error;

            if(null!=wishListResponseUICallback){
                wishListResponseUICallback.onError(error);
            }

        }else {

            wishListResultEvent.wishListResponse = wishListResponse;
            //MasterDataCache.getInstance().clearWishList();
            for (WishList wishList : wishListResponse.data){
                if(null!=wishList){
                    if(null!=wishList.listingId){
                        MasterDataCache.getInstance().addShortlistedProperty(wishList.listingId, wishList.wishListId);
                    }else if(null!=wishList.projectId){
                        MasterDataCache.getInstance().addShortlistedProperty(wishList.projectId, wishList.wishListId);
                    }
                }
            }

            if(null!=wishListResponseUICallback){
                wishListResponseUICallback.onSuccess(wishListResponse);
            }
        }

        AppBus.getInstance().post(wishListResultEvent);

    }

    @Override
    public void onError(ResponseError error) {
        WishListResultEvent wishListResultEvent = new WishListResultEvent();
        wishListResultEvent.error = error;

        if(null!=wishListResponseUICallback){
            wishListResponseUICallback.onError(error);
        }

        AppBus.getInstance().post(wishListResultEvent);
    }
}
