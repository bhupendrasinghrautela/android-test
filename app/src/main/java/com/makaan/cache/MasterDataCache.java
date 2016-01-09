package com.makaan.cache;

import android.support.annotation.NonNull;

import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.serp.FilterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Filter;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataCache {

    private static MasterDataCache instance = new MasterDataCache();
    private HashMap<Integer, ApiIntLabel> idToBuyPropertyType = new HashMap<>();
    private HashMap<Integer, ApiIntLabel> idToRentPropertyType = new HashMap<>();
    private HashMap<Integer, ApiIntLabel> idToPropertyStatus = new HashMap<>();
    private HashMap<String, String> apiLabels = new HashMap<>();
    private HashMap<String, FilterGroup> internalNameToFilterGrpBuy = new HashMap<>();
    private HashMap<String, FilterGroup> internalNameToFilterGrpRent = new HashMap<>();

    private MasterDataCache() {

    }

    public static MasterDataCache getInstance() {
        return instance;
    }

    public void addBuyPropertyType(ApiIntLabel propertyType) {
        if (null != propertyType && null != propertyType.id && null != propertyType.name) {
            idToBuyPropertyType.put(propertyType.id, propertyType);
        }
    }

    public void addRentPropertyType(ApiIntLabel propertyType) {
        if (null != propertyType && null != propertyType.id && null != propertyType.name) {
            idToRentPropertyType.put(propertyType.id, propertyType);
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

    public void addFilterGroupBuy(FilterGroup filterGroup) {
        if (null != filterGroup && null != filterGroup.internalName) {
            internalNameToFilterGrpBuy.put(filterGroup.internalName, filterGroup);
        }
    }

    public void addFilterGroupRent(FilterGroup filterGroup) {
        if (null != filterGroup && null != filterGroup.internalName) {
            internalNameToFilterGrpRent.put(filterGroup.internalName, filterGroup);
        }
    }

    public ArrayList<ApiIntLabel> getBuyPropertyTypes() {
        ArrayList<ApiIntLabel> propertyTypes = new ArrayList<>();
        propertyTypes.addAll(idToBuyPropertyType.values());
        return propertyTypes;
    }

    public ArrayList<ApiIntLabel> getRentPropertyTypes() {
        ArrayList<ApiIntLabel> propertyTypes = new ArrayList<>();
        propertyTypes.addAll(idToRentPropertyType.values());
        return propertyTypes;
    }

    public Collection<FilterGroup> getAllBuyFilterGroups() {
        return internalNameToFilterGrpBuy.values();
    }

    public Collection<FilterGroup> getAllRentFilterGroups() {
        return internalNameToFilterGrpRent.values();
    }


    public String translateApiLabel(String apiLabel) {
        return apiLabels.get(apiLabel);
    }

    public ApiIntLabel getBuyPropertyType(Integer id) {
        return idToBuyPropertyType.get(id);
    }

    public ApiIntLabel getRentPropertyType(Integer id) {
        return idToRentPropertyType.get(id);
    }

    public ApiIntLabel getPropertyStatus(Integer status) {
        return idToPropertyStatus.get(status);
    }

}
