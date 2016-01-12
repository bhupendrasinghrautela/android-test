package com.makaan.cache;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.serp.FilterGroup;
import com.makaan.util.Preference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    private HashSet<String> shortlistedProperties;

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

    public ArrayList<FilterGroup> getAllBuyFilterGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToFilterGrpBuy.values().iterator();
        ArrayList<FilterGroup> buyFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            buyFilterGroups.add(filterGroupIterator.next());
        }
        return buyFilterGroups;
    }

    public ArrayList<FilterGroup> getAllRentFilterGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToFilterGrpRent.values().iterator();
        ArrayList<FilterGroup> rentFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            rentFilterGroups.add(filterGroupIterator.next());
        }
        return rentFilterGroups;
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

    public void addShortlistedProperty(SharedPreferences preferences, String key, int id) {
        if(shortlistedProperties == null) {
            getShortlistedPropertiesFromPreferences(preferences, key);
        }
        shortlistedProperties.add(String.valueOf(id));
        addShortlistedPropertiesToPreferences(preferences.edit(), key);
    }

    public void removeShortlistedProperty(SharedPreferences preferences, String key, int id) {
        if(shortlistedProperties == null) {
            getShortlistedPropertiesFromPreferences(preferences, key);
        }
        shortlistedProperties.remove(String.valueOf(id));
        addShortlistedPropertiesToPreferences(preferences.edit(), key);
    }

    public boolean isShortlistedProperty(SharedPreferences preferences, String key, int id) {
        if(shortlistedProperties == null) {
            getShortlistedPropertiesFromPreferences(preferences, key);
        }
        if(shortlistedProperties.contains(String.valueOf(id))) {
            return true;
        }
        return false;
    }

    private void addShortlistedPropertiesToPreferences(SharedPreferences.Editor editor, String key) {
        Preference.putStringSet(editor, key, shortlistedProperties);
        editor.commit();
    }

    private void getShortlistedPropertiesFromPreferences(SharedPreferences preferences, String key) {
        shortlistedProperties = Preference.getStringSet(preferences, key);
    }
}
