package com.makaan.response.amenity;

import android.content.Intent;
import android.util.Log;

import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.makaan.constants.MessageConstants.SERP_NO_LISITINGS_AVAILABLE;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenityCallback extends JSONGetCallback {
    private static final String TAG = AmenityCallback.class.getSimpleName();

    private static final String NO_AMENITY = "No amenity available";
    private static final String GEO_DISTANCE = "geoDistance";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String NAME = "name";
    private static final String PLACE_TYPE_ID = "placeTypeId";

    @Override
    public void onSuccess(JSONObject responseObject) {
        AmenityGetEvent amenityGetEvent = new AmenityGetEvent();

        if (null != responseObject) {
            try {

                Map<Integer, AmenityCluster> amenityMap = MasterDataCache.getInstance().getAmenityMap();
                amenityGetEvent.amenityClusters = new ArrayList<>();

                JSONArray items = responseObject.getJSONArray(ResponseConstants.DATA);
                if (null != items && items.length()>0) {

                    for (int i = 0; i < items.length(); i++) {

                        JSONObject amenityJson = items.getJSONObject(i);
                        int placeTypeId = amenityJson.optInt(PLACE_TYPE_ID);
                        AmenityCluster amenityCluster = amenityMap.get(placeTypeId);
                        if(null != amenityCluster) {
                            Amenity amenity = new Amenity();
                            amenity.placeTypeId = placeTypeId;
                            amenity.lat = amenityJson.optDouble(LATITUDE);
                            amenity.lon = amenityJson.optDouble(LONGITUDE);
                            amenity.name = amenityJson.optString(NAME);
                            amenity.geoDistance = amenityJson.optDouble(GEO_DISTANCE);
                            amenity.displayDistance = amenity.geoDistance + " KM"; //TODO need to format

                            if(null!=amenityCluster.cluster){
                                amenityCluster.cluster = new ArrayList<>();
                            }

                            amenityCluster.cluster.add(amenity);
                            amenityGetEvent.amenityClusters.add(amenityCluster);
                        }
                    }

                    if(amenityGetEvent.amenityClusters.isEmpty()){
                        amenityGetEvent.message = NO_AMENITY;
                    }

                }else {
                    amenityGetEvent.message = NO_AMENITY;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse amenity data", e);
            }

        } else {
            amenityGetEvent.message = NO_AMENITY;

        }

        AppBus.getInstance().post(amenityGetEvent);
    }
}
