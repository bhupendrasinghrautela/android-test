package com.makaan.response.listing;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.listing.OtherSellersGetEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.pojo.SellerCard;
import com.makaan.response.ResponseError;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.user.Company;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vaibhav on 25/01/16.
 */
public class ListingOtherSellersCallback extends JSONGetCallback {

    private static final String TAG = ListingOtherSellersCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {
        HashMap<Long, SellerCard> sellerMap = new HashMap<>();

        try {
            JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
            JSONArray items = data.optJSONArray("items");

            JSONObject facets = data.optJSONObject("facets");
            JSONArray sellerIdFacet = facets.optJSONArray("sellerId");


            Type type = new TypeToken<ArrayList<HashMap<Long, Long>>>() {
            }.getType();
            HashMap<Long,Long> sellerPropCountMap = new HashMap<>();
            ArrayList<HashMap<Long, Long>> sellerPropCountList = new ArrayList<>();
            if(null != sellerIdFacet) {
                sellerPropCountList = MakaanBuyerApplication.gson.fromJson(sellerIdFacet.toString(), type);
                for(HashMap<Long,Long> map:sellerPropCountList){
                    sellerPropCountMap.putAll(map);
                }
            }


            Type list = new TypeToken<ArrayList<ListingDetail>>() {
            }.getType();

            ArrayList<ListingDetail> listingList = MakaanBuyerApplication.gson.fromJson(items.toString(), list);

            for (ListingDetail listingDetail : listingList) {
                Company company = listingDetail.companySeller.company;

                SellerCard sellerCard = sellerMap.get(listingDetail.sellerId);
                //SellerCard sellerCard = sellerMap.get(company.id);
                if (null == sellerCard) {
                    sellerCard = new SellerCard();
                    sellerMap.put(listingDetail.sellerId, sellerCard);
                    //sellerMap.put(company.id, sellerCard);
                    sellerCard.sellerId = listingDetail.sellerId;
                    //sellerCard.assist =
                    sellerCard.name = company.name;
                    sellerCard.type = company.type;
                    sellerCard.rating = company.score/2;
                    if(company.logo!=null){
                        sellerCard.imageUrl = company.logo;
                    }
                    else if(listingDetail.companySeller.user!=null && listingDetail.companySeller.user.profilePictureURL!=null){
                        sellerCard.imageUrl = listingDetail.companySeller.user.profilePictureURL;
                    }
                    sellerCard.noOfProperties = sellerPropCountMap.get(listingDetail.sellerId);
                }
            }

            AppBus.getInstance().post(new OtherSellersGetEvent(sellerMap.values()));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing project config", e);
        }
    }

    @Override
    public void onError(ResponseError error) {
        OtherSellersGetEvent otherSellersGetEvent = new OtherSellersGetEvent();
        otherSellersGetEvent.error = error;
        AppBus.getInstance().post(otherSellersGetEvent);
    }
}
