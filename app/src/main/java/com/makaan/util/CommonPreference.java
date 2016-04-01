package com.makaan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.makaan.pojo.VersionUpdate;
import com.makaan.response.wishlist.WishList;
import com.makaan.response.wishlist.WishListResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aishwarya on 28/03/16.
 */
public class CommonPreference {

    private static final String PREF = "makaan_buyer_user";
    private static final String PREF_MANDATORY_VERSION = "pref_mandatory_version";
    private static final String PREF_WISHLIST = "pref_wishlist";
    private static final String PREF_WISHLIST_SERVER = "pref_wishlist_server";


    public static void saveMandatoryVersion(Context context, String version) {
        Editor edit = getSharedPref(context).edit();
        edit.putString(PREF_MANDATORY_VERSION, version);
        edit.apply();
    }

    public static String getMandatoryVersion(Context context){
        VersionUpdate versionUpdate = new VersionUpdate();
        versionUpdate.setCurrentVersionCode(1);
        versionUpdate.setMandatoryVersionCode(1);
        try {
            JSONObject jsonObject = JsonBuilder.toJson(versionUpdate);
            return getSharedPref(context).getString(PREF_MANDATORY_VERSION,jsonObject.toString());
        } catch (JSONException e) {
            return getSharedPref(context).getString(PREF_MANDATORY_VERSION,null);
        }

    }

    public static void saveWishList(Context context, WishList wishList) {
        String json = getSharedPref(context).getString(PREF_WISHLIST, null);

        WishListResponse wishListResponse;
        if(json == null) {
            wishListResponse = new WishListResponse();
            wishListResponse.data = new ArrayList<>();
        }else {
            wishListResponse = (WishListResponse) JsonParser.parseJson(json, WishListResponse.class);
            if (wishListResponse == null
                    || wishListResponse.data == null) {

                wishListResponse = new WishListResponse();
                wishListResponse.data = new ArrayList<>();
            }
        }

        WishList wishListToBeRefreshed = lookupWishList(wishList, wishListResponse.data);

        if(null!=wishListToBeRefreshed){
            wishListResponse.data.remove(wishListToBeRefreshed);
            wishListResponse.data.add(wishList);
        }else {
            wishListResponse.data.add(wishList);
        }

        try {
            json = JsonBuilder.toJson(wishListResponse).toString();
            Editor edit = getSharedPref(context).edit();
            edit.putString(PREF_WISHLIST, json);
            edit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeWishList(Context context, WishList wishList){
        String json = getSharedPref(context).getString(PREF_WISHLIST, null);

        WishListResponse wishListResponse;
        if(json == null) {
            return;
        }else{
            wishListResponse = (WishListResponse) JsonParser.parseJson(json, WishListResponse.class);
            if (wishListResponse == null
                    || wishListResponse.data == null) {

                return;
            }

            WishList wishListToBeRemoved = lookupWishList(wishList, wishListResponse.data);
            if(null!=wishListToBeRemoved){
                wishListResponse.data.remove(wishListToBeRemoved);
                try {
                    json = JsonBuilder.toJson(wishListResponse).toString();
                    Editor edit = getSharedPref(context).edit();
                    edit.putString(PREF_WISHLIST, json);
                    edit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static List<WishList> getWishList(Context context){
        List<WishList> wishList = new ArrayList<>();
        String json = getSharedPref(context).getString(PREF_WISHLIST, null);
        if(json == null) return wishList;

        WishListResponse wishListResponse = (WishListResponse) JsonParser.parseJson(json, WishListResponse.class);

        if(wishListResponse != null
                && wishListResponse.data != null
                && !wishListResponse.data.isEmpty()) {

            wishList = wishListResponse.data;
            return wishList;
        }

        return wishList;
    }

    public static void clearWishList(Context context){

        WishListResponse wishListResponse = new WishListResponse();
        wishListResponse.data = new ArrayList<>();
        try {
            String json = JsonBuilder.toJson(wishListResponse).toString();
            Editor edit = getSharedPref(context).edit();
            edit.putString(PREF_WISHLIST, json);
            edit.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static WishList lookupWishList(WishList wishList, List<WishList> cachedWishList){
        if(wishList==null || cachedWishList==null || cachedWishList.isEmpty()){
            return null;
        }

        WishList wishListToBeRefreshed = null;
        if(null!=wishList.listingId){

            for (WishList wishList1 : cachedWishList){
                if(null!=wishList1.listingId){
                    if(wishList.listingId.longValue()==wishList1.listingId.longValue()){
                        wishListToBeRefreshed = wishList1;
                        break;
                    }
                }
            }

        } else if(null!=wishList.projectId){

            for (WishList wishList1 : cachedWishList){
                if(null!=wishList1.projectId){
                    if(wishList.projectId.longValue()==wishList1.projectId.longValue()){
                        wishListToBeRefreshed = wishList1;
                        break;
                    }
                }
            }
        }

        return wishListToBeRefreshed;
    }

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
