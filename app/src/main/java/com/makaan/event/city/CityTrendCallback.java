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

                    Double fromKey = 0d;

                    for (HashMap<Double, Integer> priceFacet : priceFacetsArray) {
                        for (Map.Entry<Double, Integer> facetEntry : priceFacet.entrySet()) {
                            Double toKey = facetEntry.getKey();
                            if (null != fromKey) {
                                Integer noOfListings = facetEntry.getValue();
                                CityTrendData cityTrendData = new CityTrendData(fromKey, toKey, noOfListings);
                                cityTrendDataList.add(cityTrendData);
                            }
                            fromKey = toKey;
                        }
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
