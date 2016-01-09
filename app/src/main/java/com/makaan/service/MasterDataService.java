package com.makaan.service;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.serp.FilterGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataService implements MakaanService {

    public static final String TAG = MasterDataService.class.getSimpleName();

    public void populateBuyPropertyTypes() {
        Type listType = new TypeToken<HashMap<String, String>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> propertyTypes = ( HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> propertyType : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addBuyPropertyType(new ApiIntLabel(propertyType.getValue(), Integer.parseInt(propertyType.getKey())));
                }
            }
        }, "propertyTypeBuy.json");


    }

    public void populateRentPropertyTypes() {
        Type listType = new TypeToken<HashMap<String, String>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> propertyTypes = ( HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> propertyType : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addRentPropertyType(new ApiIntLabel(propertyType.getValue(), Integer.parseInt(propertyType.getKey())));
                }
            }
        }, "propertyTypeRent.json");


    }

    public void populatePropertyStatus() {
        Type listType = new TypeToken<HashMap<String, String>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.PROPERTY_STATUS, listType, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> propertyTypes = ( HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> entry : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addPropertyStatus(new ApiIntLabel(entry.getValue(), Integer.parseInt(entry.getKey())));
                }
            }
        }, "propertyStatus.json");

    }

    public void populateApiLabels() {
        Type listType = new TypeToken<HashMap<String, String>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.API_LABEL, listType, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> apiLabels = ( HashMap<String, String>) responseObject;


                for (Map.Entry<String,String> entry : apiLabels.entrySet()) {
                    MasterDataCache.getInstance().addApiLabel(new ApiLabel(entry.getKey(), entry.getValue()));
                }
            }
        }, "apiLabels.json");


    }


    public void populateFilterGroupsBuy(){
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups  = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);

                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addFilterGroupBuy(filterGroup);
                    }
                }catch(JSONException je){
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "filterGroupBuy.json");
    }

    public void populateFilterGroupsRent(){
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups  = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);

                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addFilterGroupRent(filterGroup);
                    }
                }catch(JSONException je){
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "filterGroupRent.json");
    }
}
