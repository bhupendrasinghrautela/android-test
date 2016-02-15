package com.makaan.response.wishlist;

import com.makaan.response.ResponseError;

/**
 * Created by sunil on 15/02/16.
 */
public interface WishListResponseUICallback {
    void onSuccess(WishListResponse wishListResponse);
    void onError(ResponseError error);

}
