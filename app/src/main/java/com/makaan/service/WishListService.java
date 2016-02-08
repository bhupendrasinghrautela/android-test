package com.makaan.service;

import com.android.volley.toolbox.StringRequest;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.wishlist.WishListResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunil on 25/01/16.
 */
public class WishListService implements MakaanService {

    private static final String TAG = WishListService.class.getSimpleName();

    public void addProject(Integer projectId){
        try {
            JSONObject wishListPayload = new JSONObject();
            if(null!=projectId) {
                wishListPayload.put("projectId", projectId);
            }else {
                throw new IllegalArgumentException("Invalid arguments");
            }
            add(wishListPayload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addListing(Integer listingId){
        try {
            JSONObject wishListPayload = new JSONObject();
            if(null!=listingId){
                wishListPayload.put("listingId", listingId);
            }else {
                throw new IllegalArgumentException("Invalid arguments");
            }
            add(wishListPayload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void add(JSONObject wishListPayload){
        String requestUrl = buildWishListUrl();
        MakaanNetworkClient.getInstance().post(requestUrl, wishListPayload, new WishListResultCallback(), TAG);
    }

    public void get(){
        String requestUrl = buildWishListUrl();
        MakaanNetworkClient.getInstance().get(requestUrl, new WishListResultCallback(), TAG);
    }

    public void delete(int wishListId){
        String requestUrl = buildWishListUrl().concat("/" + wishListId);
        MakaanNetworkClient.getInstance().delete(requestUrl, null, new WishListResultCallback(), TAG);
    }

    private String buildWishListUrl(){
        
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(ApiConstants.BASE_URL);
        requestBuilder.append("/data/v1/entity/user/wish-list");

        return requestBuilder.toString();
    }

}
