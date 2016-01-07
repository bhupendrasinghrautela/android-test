package com.makaan.cache;

import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.serp.FilterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Filter;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataCache {

    private static MasterDataCache instance = new MasterDataCache();
    private HashMap<Integer, ApiIntLabel> idToPropertyType = new HashMap<>();
    private HashMap<Integer, ApiIntLabel> idToPropertyStatus = new HashMap<>();
    private HashMap<String, String> apiLabels = new HashMap<>();
    private HashMap<String, FilterGroup> internalNameToFilterGrp = new HashMap<>();

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

    public void addFilterGroup(FilterGroup filterGroup){
        if(null != filterGroup && null != filterGroup.internalName){
            internalNameToFilterGrp.put(filterGroup.internalName, filterGroup);
        }
    }

    public Collection<FilterGroup> getAllFilterGroups(){
        return internalNameToFilterGrp.values();
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
