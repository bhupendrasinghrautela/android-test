package com.makaan.service;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.analytics.SerpFilterMessageMap;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.pojo.ConstructionStatus;
import com.makaan.pojo.VersionUpdate;
import com.makaan.response.ResponseError;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.master.ApiLabel;
import com.makaan.response.master.MasterFurnishing;
import com.makaan.response.master.MasterSpecification;
import com.makaan.response.master.PropertyAmenity;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.ListingInfoMap;
import com.makaan.util.CommonPreference;
import com.makaan.util.JsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhav on 29/12/15.
 */
public class MasterDataService implements MakaanService {

    public static final String TAG = MasterDataService.class.getSimpleName();


    public void populateMasterSpecifications() {

        Type listType = new TypeToken<ArrayList<MasterSpecification>>() {
        }.getType();
        MakaanNetworkClient.getInstance().get(ApiConstants.MASTER_FURNISHINGS, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<MasterSpecification> masterSpecifications = (ArrayList<MasterSpecification>) responseObject;

                for (MasterSpecification masterSpecification : masterSpecifications) {
                    MasterDataCache.getInstance().addMasterSpecification(masterSpecification);
                }
            }
        }, true);
    }


    public void populateMasterFunishings() {

        Type listType = new TypeToken<ArrayList<MasterFurnishing>>() {
        }.getType();
        MakaanNetworkClient.getInstance().get(ApiConstants.MASTER_FURNISHINGS, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<MasterFurnishing> masterFurnishings = (ArrayList<MasterFurnishing>) responseObject;

                for (MasterFurnishing masterFurnishing : masterFurnishings) {
                    MasterDataCache.getInstance().addMasterFurnishing(masterFurnishing);
                }
            }
        }, true);
    }

    public void populatePropertyAmenities() {

        Type listType = new TypeToken<ArrayList<PropertyAmenity>>() {
        }.getType();
        MakaanNetworkClient.getInstance().get(ApiConstants.PROPERTY_AMENITY, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<PropertyAmenity> propertyAmenities = (ArrayList<PropertyAmenity>) responseObject;

                for (PropertyAmenity propertyAmenity : propertyAmenities) {
                    MasterDataCache.getInstance().addPropertyAmenity(propertyAmenity);
                }
            }
        }, true);
    }

    public void populateBuyPropertyTypes() {
        Type listType = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                LinkedHashMap<String, String> propertyTypes = (LinkedHashMap<String, String>) responseObject;

                for (Map.Entry<String, String> propertyType : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addBuyPropertyType(new ApiIntLabel(propertyType.getValue(), Integer.parseInt(propertyType.getKey())));
                }
            }
        }, "propertyTypeBuy.json");


    }

    public void populateRentPropertyTypes() {
        Type listType = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                LinkedHashMap<String, String> propertyTypes = (LinkedHashMap<String, String>) responseObject;

                for (Map.Entry<String, String> propertyType : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addRentPropertyType(new ApiIntLabel(propertyType.getValue(), Integer.parseInt(propertyType.getKey())));
                }
            }
        }, "propertyTypeRent.json");

    }

    public void populateBhkList() {
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.UNIT_TYPE, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> bhks = (HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> bhk : bhks.entrySet()) {
                    MasterDataCache.getInstance().addBhkList(new ApiIntLabel(bhk.getValue(), Integer.parseInt(bhk.getKey())));
                }
            }
        }, "bhkList.json");

    }

    public void populatePropertyStatus() {
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.PROPERTY_STATUS, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> propertyTypes = (HashMap<String, String>) responseObject;

                for (Map.Entry<String, String> entry : propertyTypes.entrySet()) {
                    MasterDataCache.getInstance().addPropertyStatus(new ApiIntLabel(entry.getValue(), Integer.parseInt(entry.getKey())));
                }
            }
        }, "propertyStatus.json");

    }

    public void populatePropertyDisplayOrder() {
        final Type propertyDisplay = new TypeToken<HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.PROPERTY_DISPLAY_ORDER, propertyDisplay, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                MasterDataCache.getInstance().addPropertyDisplayOrder((Map<String, Map<String, Map<String, List<String>>>>) responseObject);
            }
        }, "propertyPageDisplayOrder.json");

    }

    public void populateDefaultAmenityIds() {
        final Type amenityDefault = new TypeToken<ArrayList<Long>>(){
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.DEFAULT_AMENITY, amenityDefault, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                MasterDataCache.getInstance().addDefaultAmenities((List<Long>) responseObject);
            }
        }, "defaultAmenityIdList.json",true);

    }

    public void populateApiLabels() {
        Type listType = new TypeToken<HashMap<String, String>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.API_LABEL, listType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, String> apiLabels = (HashMap<String, String>) responseObject;


                for (Map.Entry<String, String> entry : apiLabels.entrySet()) {
                    MasterDataCache.getInstance().addApiLabel(new ApiLabel(entry.getKey(), entry.getValue()));
                }
            }
        }, "apiLabels.json");


    }


    public void populateFilterGroupsBuy() {
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {
        }.getType();


        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {

                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);


                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addFilterGroupBuy(filterGroup);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "filterGroupBuy.json");
    }


    public void populateFilterGroupsRent() {
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);

                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addFilterGroupRent(filterGroup);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "filterGroupRent.json");
    }
    public void populatePyrGroupsBuy() {
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {
        }.getType();


        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {

                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);


                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addPyrGroupBuy(filterGroup);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse pyr groups", je);
                }

            }


        }, "pyrGroupBuy.json");
    }


    public void populatePyrGroupsRent() {
        final Type filterGroupTypeList = new TypeToken<ArrayList<FilterGroup>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.FILTER_GROUP, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);
                    JSONArray filters = object.getJSONArray(ResponseConstants.FILTERS);

                    ArrayList<FilterGroup> filterGroups = MakaanBuyerApplication.gson.fromJson(filters.toString(), filterGroupTypeList);

                    for (FilterGroup filterGroup : filterGroups) {
                        MasterDataCache.getInstance().addPyrGroupRent(filterGroup);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse pyr groups", je);
                }

            }


        }, "pyrGroupRent.json");
    }

    public void populateLocalityAmenityMap() {
        final Type amenityClusterTypeList = new TypeToken<ArrayList<AmenityCluster>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.AMENITY_LOCALITY, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray amenities = responseObject.getJSONArray(ResponseConstants.DATA);

                    ArrayList<AmenityCluster> amenityClusters =
                            MakaanBuyerApplication.gson.fromJson(amenities.toString(), amenityClusterTypeList);

                    for (AmenityCluster amenityCluster : amenityClusters) {
                        MasterDataCache.getInstance().addLocalityAmenityCluster(amenityCluster);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "amenityLocalityMap.json");
    }

    public void populateProjectAmenityMap() {
        final Type amenityClusterTypeList = new TypeToken<ArrayList<AmenityCluster>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.AMENITY_PROJECT, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray amenities = responseObject.getJSONArray(ResponseConstants.DATA);

                    ArrayList<AmenityCluster> amenityClusters =
                            MakaanBuyerApplication.gson.fromJson(amenities.toString(), amenityClusterTypeList);

                    for (AmenityCluster amenityCluster : amenityClusters) {
                        MasterDataCache.getInstance().addProjectAmenityCluster(amenityCluster);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        }, "amenityProjectMap.json");
    }

    public void populateConstructionStatus() {
        final Type ConstructionStatusList = new TypeToken<ArrayList<ConstructionStatus>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.CONSTRUCTION_STATUS, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray amenities = responseObject.getJSONArray(ResponseConstants.DATA);

                    ArrayList<ConstructionStatus> amenityClusters =
                            MakaanBuyerApplication.gson.fromJson(amenities.toString(), ConstructionStatusList);

                    for (ConstructionStatus constructionStatus : amenityClusters) {
                        MasterDataCache.getInstance().addConstructionStatus(constructionStatus);
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse filters", je);
                }

            }


        });
    }


    public void populateSearchType() {
        Type searchType = new TypeToken<HashMap<String, ApiLabel>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.SEARCH_TYPE, searchType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                try {

                    HashMap<String, ApiLabel> searchTypeMap = (HashMap<String, ApiLabel>) responseObject;
                    MasterDataCache.getInstance().setSearchType(searchTypeMap);


                    /*JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);

                    Map<String, Object> retMap =
                            MakaanBuyerApplication.gson.fromJson(
                                    object.toString(), new TypeToken<HashMap<String, Object>>() {
                            }.getType());



                    for (Map.Entry<String, JSONObject> searchType : searchTypes.entrySet()) {
                        ApiLabel data =
                                MakaanBuyerApplication.gson.fromJson(
                                        searchType.getValue().toString(), ApiLabel.class);
                        MasterDataCache.getInstance().addSearchType(searchType.getKey(), data);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "searchResultType.json");

    }

    public void populateJarvisMessageType() {
        Type jarvisMessageType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.JARVIS_MESSAGE_TYPE, jarvisMessageType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, Integer> jarvisMessageTypes = (HashMap<String, Integer>) responseObject;

                MasterDataCache.getInstance().addJarvisMessageType(jarvisMessageTypes);
            }
        }, "jarvisMessageType.json");

    }


    public void populateJarvisCtaMessageType() {
        Type jarvisMessageType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.JARVIS_CTA_MESSAGE_TYPE, jarvisMessageType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, Integer> jarvisMessageTypes = (HashMap<String, Integer>) responseObject;

                MasterDataCache.getInstance().addJarvisCtaMessageType(jarvisMessageTypes);
            }
        }, "jarvisCtaMessageType.json");
    }

    public void populateJarvisSerpFilterMessageMap() {
        Type jarvisMessageType = new TypeToken<HashMap<String, SerpFilterMessageMap>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.JARVIS_SERP_FILTER_MESSAGE_MAP, jarvisMessageType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, SerpFilterMessageMap> serpFilterMessageMap = (HashMap<String, SerpFilterMessageMap>) responseObject;

                MasterDataCache.getInstance().addJarvisSerpFilterMessageMap(serpFilterMessageMap);
            }
        }, "jarvisSerpFilterType.json");
    }

    public void populateBuyerJourneyMessageMap() {
        Type jarvisMessageType = new TypeToken<HashMap<String, BuyerJourneyMessage>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.JARVIS_SERP_FILTER_MESSAGE_MAP, jarvisMessageType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {}

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                HashMap<String, BuyerJourneyMessage> buyerJourneyMessageMap = (HashMap<String, BuyerJourneyMessage>) responseObject;

                MasterDataCache.getInstance().addJarvisBuyerJourneyMessageMap(buyerJourneyMessageMap);
            }
        }, "jarvisBuyerJourneyMessageType.json");
    }


    public void populateListingInfoList() {

        final Type listingInfoTypeList = new TypeToken<ListingInfoMap>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(ApiConstants.LISTING_INFO_MAP, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONObject object = responseObject.getJSONObject(ResponseConstants.DATA);

                    ListingInfoMap listingInfoMap = MakaanBuyerApplication.gson.fromJson(object.toString(), listingInfoTypeList);
                    MasterDataCache.getInstance().addListingInfoMap(listingInfoMap);
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse Listing info map", je);
                }

            }


        }, "listingInfoMap.json");
    }

    public void populateDirectionList() {


        MakaanNetworkClient.getInstance().get(ApiConstants.DIRECTIONS, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray jsonArray = responseObject.getJSONArray(ResponseConstants.DATA);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MasterDataCache.getInstance().addDirection(obj.getInt(ResponseConstants.ID), obj.getString(ResponseConstants.DIRECTION));
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse Direction info map", je);
                }
            }
        }, null);
    }

    public void populateOwnershipTypeList() {
        MakaanNetworkClient.getInstance().get(ApiConstants.OWNERSHIP_TYPE, new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {}

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray jsonArray = responseObject.getJSONArray(ResponseConstants.DATA);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        MasterDataCache.getInstance().addOwnershipType(obj.getInt(ResponseConstants.ID), obj.getString(ResponseConstants.DISPLAY_NAME));
                    }
                } catch (JSONException je) {
                    Log.e(TAG, "Unable to parse Direction info map", je);
                }
            }
        }, null);
    }

    public void checkMandatoryVersion(final Context context) {
        final Type type = VersionUpdate.class;
        MakaanNetworkClient.getInstance().get(ApiConstants.VERSION_UPDATE_URL, type, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {

            }

            @Override
            public void onSuccess(Object responseObject) {

                try {
                    VersionUpdate versionUpdate = (VersionUpdate) responseObject;
                    JSONObject  jsonObject = JsonBuilder.toJson(versionUpdate);
                    CommonPreference.saveMandatoryVersion(context, jsonObject.toString());
                } catch (Exception e) {
                }
            }
        });
    }
}
