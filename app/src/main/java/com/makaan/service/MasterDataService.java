package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.serp.FilterGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataService implements MakaanService {

    public void populatePropertyTypes() {
        Type listType = new TypeToken<HashMap<String, String>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> propertyTypes = ( HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> propertyType : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addPropertyType(new ApiIntLabel(propertyType.getValue(), Integer.parseInt(propertyType.getKey())));
                }
            }
        }, "propertyType.json");


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


    public void populateFilterGroups(){
        Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {}.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, filterGroupTypeList, new ObjectGetCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<FilterGroup> filterGroups = ( ArrayList<FilterGroup>) responseObject;


                for (FilterGroup filterGroup : filterGroups) {
                    MasterDataCache.getInstance().addFilterGroup(filterGroup);
                }
            }
        }, "filterGroup.json");
    }
}
