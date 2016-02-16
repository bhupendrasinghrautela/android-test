package com.makaan.response.wishlist;

import android.util.Log;

import com.android.volley.Request;
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
    private int requestMethod = -1;
    private Long itemId;

    public WishListResultCallback(int requestMethod){
        this.requestMethod = requestMethod;
    }

    public WishListResultCallback(int requestMethod, WishListResponseUICallback wishListResponseUICallback){
        this.requestMethod = requestMethod;
        this.wishListResponseUICallback = wishListResponseUICallback;
    }

    public void setItemId(Long itemId){
        this.itemId = itemId;
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

            if(Request.Method.GET==requestMethod || Request.Method.POST==requestMethod) {
                wishListResultEvent.wishListResponse = wishListResponse;
                MasterDataCache.getInstance().clearWishList();
                for (WishList wishList : wishListResponse.data) {
                    if (null != wishList) {
                        if (null != wishList.listingId) {
                            MasterDataCache.getInstance().addShortlistedProperty(wishList.listingId, wishList.wishListId);
                        } else if (null != wishList.projectId) {
                            MasterDataCache.getInstance().addShortlistedProperty(wishList.projectId, wishList.wishListId);
                        }
                    }
                }
            }else  if (Request.Method.DELETE==requestMethod){
                MasterDataCache.getInstance().removeShortlistedProperty(itemId);
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
