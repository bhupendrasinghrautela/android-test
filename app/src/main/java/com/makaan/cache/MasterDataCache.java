package com.makaan.cache;


import android.util.SparseArray;

import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.analytics.SerpFilterMessageMap;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.master.MasterFurnishing;
import com.makaan.response.master.MasterSpecification;
import com.makaan.response.master.PropertyAmenity;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.ListingInfoMap;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.user.UserResponse.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataCache {

    private static MasterDataCache instance = new MasterDataCache();

    private LinkedHashMap<Integer, ApiIntLabel> idToBuyPropertyType = new LinkedHashMap<>();
    private LinkedHashMap<Integer, ApiIntLabel> idToRentPropertyType = new LinkedHashMap<>();

    private HashMap<Integer, ApiIntLabel> idToPropertyStatus = new HashMap<>();
    private Map<Integer, ApiIntLabel> idToBhk = new TreeMap<>();
    private HashMap<String, String> apiLabels = new HashMap<>();
    private HashMap<Long, PropertyAmenity> idToPropertyAmenity = new HashMap<>();
    private HashMap<Long, MasterFurnishing> idToMasterFurnishing = new HashMap<>();
    private HashMap<Long, MasterSpecification> idToMasterSpecification = new HashMap<>();

    private HashMap<String, FilterGroup> internalNameToFilterGrpBuy = new HashMap<>();
    private HashMap<String, FilterGroup> internalNameToFilterGrpRent = new HashMap<>();

    private HashMap<String, FilterGroup> internalNameToPyrGrpBuy = new HashMap<>();
    private HashMap<String, FilterGroup> internalNameToPyrGrpRent = new HashMap<>();
    private Map<Integer, AmenityCluster> amenityMap = new HashMap<>();
    private Map<String, ApiLabel> searchTypeMap = new HashMap<>();
    private Map<String, Map<String, Map<String, List<String>>>> propertyDisplayOrder = new HashMap<>();
    private List<Long> defaultAmenityList;

    private Map<String, Integer> jarvisMessageTypeMap = new HashMap<>();
    private Map<String, Integer> jarvisCtaMessageTypeMap = new HashMap<>();
    private Map<String, SerpFilterMessageMap> jarvisSerpFilterMessageMap = new HashMap<>();
    private Map<String, BuyerJourneyMessage> jarvisBuyerJourneyMessageMap = new HashMap<>();

    private HashMap<Long, Long> userWishListMap;
    private ArrayList<SaveSearch> userSavedSearches;
    private ListingInfoMap listingInfoMap;
    private SparseArray<String> directionApiList = new SparseArray<>();
    private SparseArray<String> ownershipTypeApiList = new SparseArray<>();
    private UserData userData;

    private MasterDataCache() {

    }

    public static MasterDataCache getInstance() {
        return instance;
    }

    public void addMasterFurnishing(MasterFurnishing masterFurnishing) {

        if (null != masterFurnishing && null != masterFurnishing.id && null != masterFurnishing.name) {
            idToMasterFurnishing.put(masterFurnishing.id, masterFurnishing);
        }
    }

    public MasterSpecification getMasterSpecificationById(Long masterSpecId) {
        return idToMasterSpecification.get(masterSpecId);
    }

    public MasterFurnishing getMasterFurnishingById(Long furnishingId) {
        return idToMasterFurnishing.get(furnishingId);
    }

    public void addMasterSpecification(MasterSpecification masterSpecification) {
        if (null != masterSpecification && null != masterSpecification.masterSpecId && null != masterSpecification.masterSpecClassName) {
            idToMasterSpecification.put(masterSpecification.masterSpecId, masterSpecification);
        }
    }


    public void addPropertyAmenity(PropertyAmenity propertyAmenity) {

        if (null != propertyAmenity && null != propertyAmenity.amenityId && null != propertyAmenity.amenityName) {
            idToPropertyAmenity.put(propertyAmenity.amenityId, propertyAmenity);
        }
    }

    public PropertyAmenity getPropertyAmenityById(Long propAmenityId) {
        return idToPropertyAmenity.get(propAmenityId);
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
            if (filterGroup.rangeFilterValues.size() > 0) {
                for (RangeFilter filter : filterGroup.rangeFilterValues) {
                    filter.selectedMinValue = filter.minValue;
                    filter.selectedMaxValue = filter.maxValue;
                }
            }
        }
    }

    public void addFilterGroupRent(FilterGroup filterGroup) {
        if (null != filterGroup && null != filterGroup.internalName) {
            internalNameToFilterGrpRent.put(filterGroup.internalName, filterGroup);
            if (filterGroup.rangeFilterValues.size() > 0) {
                for (RangeFilter filter : filterGroup.rangeFilterValues) {
                    filter.selectedMinValue = filter.minValue;
                    filter.selectedMaxValue = filter.maxValue;
                }
            }
        }
    }

    public void addPyrGroupBuy(FilterGroup filterGroup) {
        if (null != filterGroup && null != filterGroup.internalName) {
            internalNameToPyrGrpBuy.put(filterGroup.internalName, filterGroup);
            if(filterGroup.rangeFilterValues.size() > 0) {
                for(RangeFilter filter : filterGroup.rangeFilterValues) {
                    filter.selectedMinValue = filter.minValue;
                    filter.selectedMaxValue = filter.maxValue;
                }
            }
        }
    }

    public void addPyrGroupRent(FilterGroup filterGroup) {
        if (null != filterGroup && null != filterGroup.internalName) {
            internalNameToPyrGrpRent.put(filterGroup.internalName, filterGroup);
            if(filterGroup.rangeFilterValues.size() > 0) {
                for(RangeFilter filter : filterGroup.rangeFilterValues) {
                    filter.selectedMinValue = filter.minValue;
                    filter.selectedMaxValue = filter.maxValue;
                }
            }
        }
    }

    public void addAmenityCluster(AmenityCluster amenityCluster) {
        if (null != amenityCluster) {
            amenityMap.put(amenityCluster.placeTypeId, amenityCluster);
        }
    }

    public void addBhkList(ApiIntLabel apiIntLabel) {
        if (null != apiIntLabel) {
            idToBhk.put(apiIntLabel.id, apiIntLabel);
        }
    }

    public void addSearchType(String type, ApiLabel searchType) {
        if (null != searchType && null != type && null != searchType) {
            searchTypeMap.put(type, searchType);
        }
    }

    public void setSearchType(Map<String, ApiLabel> searchTypeMap) {
        this.searchTypeMap = searchTypeMap;
    }

    public void addPropertyDisplayOrder(Map<String, Map<String, Map<String, List<String>>>> map) {
        propertyDisplayOrder = map;
    }

    public void addJarvisMessageType(Map<String, Integer> map) {
        jarvisMessageTypeMap = map;
    }

    public void addJarvisCtaMessageType(Map<String, Integer> map) {
        jarvisCtaMessageTypeMap = map;
    }

    public void addJarvisSerpFilterMessageMap(Map<String, SerpFilterMessageMap> map) {
        jarvisSerpFilterMessageMap = map;
    }

    public void addJarvisBuyerJourneyMessageMap(Map<String, BuyerJourneyMessage> map) {
        jarvisBuyerJourneyMessageMap = map;
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

    public ArrayList<ApiIntLabel> getBhkList() {

        ArrayList<ApiIntLabel> bhkList = new ArrayList<>();
        bhkList.addAll(idToBhk.values());
        return bhkList;
    }


    public ArrayList<FilterGroup> getAllBuyFilterGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToFilterGrpBuy.values().iterator();
        ArrayList<FilterGroup> buyFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            buyFilterGroups.add(filterGroupIterator.next());
        }
        Collections.sort(buyFilterGroups, new Comparator<FilterGroup>() {
            @Override
            public int compare(FilterGroup lhs, FilterGroup rhs) {
                return lhs.displayOrder - rhs.displayOrder;
            }
        });
        return buyFilterGroups;
    }

    public ArrayList<FilterGroup> getAllRentFilterGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToFilterGrpRent.values().iterator();
        ArrayList<FilterGroup> rentFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            rentFilterGroups.add(filterGroupIterator.next());
        }
        Collections.sort(rentFilterGroups, new Comparator<FilterGroup>() {
            @Override
            public int compare(FilterGroup lhs, FilterGroup rhs) {
                return lhs.displayOrder - rhs.displayOrder;
            }
        });
        return rentFilterGroups;
    }

    public Map<String, Integer> getJarvisMessageTypeMap(){
        return jarvisMessageTypeMap;
    }

    public Map<String, Integer> getJarvisCtaMessageTypeMap(){
        return jarvisCtaMessageTypeMap;
    }

    public Map<String, SerpFilterMessageMap> getSerpFilterMessageMap(){ return  jarvisSerpFilterMessageMap; }
    public Map<String, BuyerJourneyMessage> getJarvisBuyerJourneyMessageMap(){ return  jarvisBuyerJourneyMessageMap; }


    public ArrayList<FilterGroup> getAllBuyPyrGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToPyrGrpBuy.values().iterator();
        ArrayList<FilterGroup> buyFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            buyFilterGroups.add(filterGroupIterator.next());
        }
        return buyFilterGroups;
    }

    public ArrayList<FilterGroup> getAllRentPyrGroups() {
        Iterator<FilterGroup> filterGroupIterator = internalNameToPyrGrpRent.values().iterator();
        ArrayList<FilterGroup> rentFilterGroups = new ArrayList<>();
        while (filterGroupIterator.hasNext()) {
            rentFilterGroups.add(filterGroupIterator.next());
        }
        return rentFilterGroups;
    }


    public Map<Integer, AmenityCluster> getAmenityMap() {
        return amenityMap;
    }

    public Map<String, ApiLabel> getSearchTypeMap() {
        return searchTypeMap;
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

    public void addShortlistedProperty(Long id, Long wishlistId) {

        if(null==userWishListMap) {
            userWishListMap = new HashMap<>();
        }

        userWishListMap.put(id, wishlistId);

    }

    public void removeShortlistedProperty(long id) {
        if(null!=userWishListMap) {
            userWishListMap.remove(id);
        }
    }

    public void clearWishList(){
        if(null!=userWishListMap) {
            userWishListMap.clear();
        }
    }

    public void clearSavedSearches(){
        if(null != userSavedSearches) {
            userSavedSearches.clear();
        }
    }

    public void addSavedSearch(SaveSearch savedSearch) {
        if(userSavedSearches == null) {
            userSavedSearches = new ArrayList<>();
        }
        userSavedSearches.add(savedSearch);
    }

    public ArrayList<SaveSearch> getSavedSearch() {
        if(userSavedSearches != null) {
            ArrayList<SaveSearch> saveSearches = new ArrayList<>();
            saveSearches.addAll(userSavedSearches);
            return saveSearches;
        }
        return null;
    }

    public boolean isShortlistedProperty(Long id) {

        if(null!=userWishListMap) {
            if (userWishListMap.containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    public Long getWishlistId(Long id) {

        if(null!=userWishListMap) {
            if (userWishListMap.containsKey(id)) {
                return userWishListMap.get(id);
            }
        }
        return null;
    }

    public List<String> getDisplayOrder(String category, String type, String card) {
        if(propertyDisplayOrder.get(category)==null){
            return propertyDisplayOrder.get("Primary").get("Apartment").get(card);
        }
        if(propertyDisplayOrder.get(category).get(type)==null){
            return propertyDisplayOrder.get("Primary").get("Apartment").get(card);
        }
        return propertyDisplayOrder.get(category).get(type).get(card);
    }

    public void addDefaultAmenities(List<Long> defaultAmenityList) {
        this.defaultAmenityList = defaultAmenityList;
    }

    public HashMap<Long,Boolean> getDefaultAmenityList(){
        HashMap<Long, Boolean> map = new HashMap<>();
        for(Long id:defaultAmenityList){
            map.put(id,false);
        }
        return map;
    }

    public void addListingInfoMap(ListingInfoMap listingInfoMap) {
        this.listingInfoMap = listingInfoMap;
        this.listingInfoMap.initialize();
    }

    public ArrayList<ListingInfoMap.InfoMap> getListingMapInfo(int type) {
        return listingInfoMap.map.get(type);
    }

    public void addDirection(int id, String direction) {
        directionApiList.put(id, direction);
    }

    public String getDirection(int id) {
        return directionApiList.get(id);
    }

    public void addOwnershipType(int id, String direction) {
        ownershipTypeApiList.put(id, direction);
    }

    public String getOwnershipType(int id) {
        return ownershipTypeApiList.get(id);
    }

    public UserData getUserData(){
        return userData;
    }

    public void setUserData(UserData userData){
        this.userData = userData;
    }
}
