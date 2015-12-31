package com.makaan.cache;

import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;

import java.util.HashMap;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataCache {

    private static MasterDataCache instance = new MasterDataCache();
    private HashMap<Integer, ApiIntLabel> idToPropertyType = new HashMap<>();
    private HashMap<Integer, ApiIntLabel> idToPropertyStatus = new HashMap<>();
    private HashMap<String, String> apiLabels = new HashMap<>();

    private MasterDataCache() {

    }

    public static MasterDataCache getInstance() {
        return instance;
    }

    public void addPropertyType(ApiIntLabel propertyType) {
        if (null != propertyType && null != propertyType.id && null != propertyType.name) {
            idToPropertyType.put(propertyType.id, propertyType);
        }
    }

    public void addPropertyStatus(ApiIntLabel propertyType) {
        if (null != propertyType && null != propertyType.id && null != propertyType.name) {
            idToPropertyStatus.put(propertyType.id, propertyType);
        }
    }


    public void addApiLabel(ApiLabel apiLabel) {
        if (null != apiLabel && null != apiLabel.key && null != apiLabel.value) {
            apiLabels.put(apiLabel.key, apiLabel.value);
        }
    }

    public String translateApiLabel(String apiLabel) {
        return apiLabels.get(apiLabel);
    }

    public ApiIntLabel getPropertyType(Integer id) {
        return idToPropertyType.get(id);
    }

    public ApiIntLabel getPropertyStatus(Integer status){
        return idToPropertyStatus.get(status);
    }

}
