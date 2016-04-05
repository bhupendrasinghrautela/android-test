package com.makaan.service;

import com.android.volley.Request;
import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.ResponseError;
import com.makaan.response.wishlist.WishList;
import com.makaan.response.wishlist.WishListResponseUICallback;
import com.makaan.response.wishlist.WishListResultCallback;
import com.makaan.ui.view.WishListButton;
import com.makaan.util.CommonPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void syncWishList(){
        try {
            List<WishList> allWishList = MasterDataCache.getInstance().getAllWishList();
            if(null == allWishList || allWishList.isEmpty()){
                return;
            }

            JSONArray jsonArray = new JSONArray();
            for (WishList wishList : allWishList) {
                if (wishList.dirtyFlag == WishListButton.WishListStatusFlag.toAdd) {

                    JSONObject wishListPayload = null;
                    if (null != wishList.listingId) {
                        wishListPayload = new JSONObject();
                        wishListPayload.put("listingId", wishList.listingId);
                        wishListPayload.put("projectId", wishList.projectId);
                    } else if (null != wishList.projectId) {
                        wishListPayload = new JSONObject();
                        wishListPayload.put("projectId", wishList.projectId);
                    }

                    if (null != wishListPayload) {
                        jsonArray.put(wishListPayload);
                    }
                }
            }

            if(jsonArray.length()>0){
                MakaanNetworkClient.getInstance().postWithArray(ApiConstants.WISHLIST_V2, jsonArray,
                        new WishListResultCallback(Request.Method.POST), TAG);
            }

        }catch (Exception e){}
    }

    private void add(JSONObject wishListPayload, WishListResultCallback callback){
        MakaanNetworkClient.getInstance().post(ApiConstants.WISHLIST, wishListPayload, callback, TAG);
    }

    public void get(){
        MakaanNetworkClient.getInstance().get(ApiConstants.WISHLIST, new WishListResultCallback(Request.Method.GET), TAG);
    }

    public void delete(Long itemId, WishListResponseUICallback callback){
        Long wishListId = MasterDataCache.getInstance().getWishListId(itemId);
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

}
