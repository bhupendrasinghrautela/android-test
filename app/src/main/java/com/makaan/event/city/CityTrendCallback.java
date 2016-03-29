package com.makaan.event.city;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.city.CityTrendData;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 22/01/16.
 */
public class CityTrendCallback extends JSONGetCallback {

    public static final String TAG = CityTrendCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {

        try {
            JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
            JSONObject priceFacets = data.getJSONObject(ResponseConstants.FACETS);
            if (null != priceFacets) {
                ArrayList<CityTrendData> cityTrendDataList = new ArrayList<>();
                JSONArray priceJsonArray = priceFacets.optJSONArray(ResponseConstants.PRICE);
                if (null != priceJsonArray) {
                    Type cityTrendType = new TypeToken<ArrayList<HashMap<Double, Integer>>>() {
                    }.getType();
                    ArrayList<HashMap<Double, Integer>> priceFacetsArray = MakaanBuyerApplication.gson.fromJson(priceJsonArray.toString(), cityTrendType);

                    CityTrendData lastTrendData = null;
                    Double diff= null;
                    for (HashMap<Double, Integer> priceFacet : priceFacetsArray) {
                        for (Map.Entry<Double, Integer> facetEntry : priceFacet.entrySet()) {
                            Double fromKey = facetEntry.getKey();
                            if (null != fromKey) {
                                if(lastTrendData!=null){
                                    lastTrendData.maxPrice = fromKey;
                                    diff = lastTrendData.maxPrice-lastTrendData.minPrice;
                                }
                                CityTrendData cityTrendData = new CityTrendData();
                                cityTrendData.minPrice = fromKey;
                                cityTrendData.noOfListings = facetEntry.getValue();
                                cityTrendDataList.add(cityTrendData);
                                lastTrendData = cityTrendData;
                            }
                        }
                    }
                    if(lastTrendData!=null && diff!=null) {
                        lastTrendData.maxPrice = lastTrendData.minPrice + diff;
                    }
                }
                AppBus.getInstance().post(new CityTrendEvent(cityTrendDataList));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing city Trend Data", e);
        }
    }

    @Override
    public void onError(ResponseError error) {

        CityTrendEvent cityTrendDataList = new CityTrendEvent();
        cityTrendDataList.error = error;
        AppBus.getInstance().post(cityTrendDataList);

    }
}
