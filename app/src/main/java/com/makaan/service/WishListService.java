package com.makaan.service;

import com.android.volley.Request;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.ResponseError;
import com.makaan.response.wishlist.WishListResponseUICallback;
import com.makaan.response.wishlist.WishListResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListService implements MakaanService {

    private static final String TAG = WishListService.class.getSimpleName();

    private long entityIdToBeShortlisted = -1;

    public void addProject(Long projectId, WishListResponseUICallback callback){
        try {
            JSONObject wishListPayload = new JSONObject();
            if(null!=projectId) {
                wishListPayload.put("projectId", projectId);
            }else {
                throw new IllegalArgumentException("Invalid arguments");
            }
            add(wishListPayload, new WishListResultCallback(Request.Method.POST, callback));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addListing(Long listingId, Long projectId, WishListResponseUICallback callback){
        try {
            JSONObject wishListPayload = new JSONObject();
            if(null!=listingId){
                wishListPayload.put("listingId", listingId);
                wishListPayload.put("projectId", projectId);
            }else {
                throw new IllegalArgumentException("Invalid arguments");
            }
            add(wishListPayload, new WishListResultCallback(Request.Method.POST,callback));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void add(JSONObject wishListPayload, WishListResultCallback callback){
//        String requestUrl = buildWishListUrl();
        MakaanNetworkClient.getInstance().post(ApiConstants.WISHLIST, wishListPayload, callback, TAG);
    }

    public void get(){
//        String requestUrl = buildWishListUrl();
        MakaanNetworkClient.getInstance().get(ApiConstants.WISHLIST, new WishListResultCallback(Request.Method.GET), TAG);
    }

    public void delete(Long itemId, WishListResponseUICallback callback){
        Long wishListId = MasterDataCache.getInstance().getWishlistId(itemId);
        if(null!=wishListId) {
            String requestUrl = ApiConstants.WISHLIST.concat("/" + wishListId);
            WishListResultCallback wishListResultCallback = new WishListResultCallback(Request.Method.DELETE, callback);
            wishListResultCallback.setItemId(itemId);
            MakaanNetworkClient.getInstance().delete(requestUrl, null, wishListResultCallback, TAG);
        }else {
            ResponseError error = new ResponseError();
            error.msg = "";
            if(null!=callback){
                callback.onError(error);
            }
        }
    }

    public long getEntityIdToBeShortlisted() {
        return entityIdToBeShortlisted;
    }

    public void setEntityIdToBeShortlisted(long entityIdToBeShortlisted) {
        this.entityIdToBeShortlisted = entityIdToBeShortlisted;
    }

    /*private String buildWishListUrl(){

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(ApiConstants.WISHLIST);
        requestBuilder.append("/petra/data/v1/entity/user/wish-list");

        return requestBuilder.toString();
    }*/

}
