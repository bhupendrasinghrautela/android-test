package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.RequestConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.cookie.Session;
import com.makaan.event.location.LocationGetEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.locality.Locality;
import com.makaan.response.location.MyLocation;
import com.makaan.response.search.SearchResponse;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.CITY;
import static com.makaan.constants.RequestConstants.CITY_ID;
import static com.makaan.constants.RequestConstants.GEO_DISTANCE;
import static com.makaan.constants.RequestConstants.LABEL;
import static com.makaan.constants.RequestConstants.LATITUDE;
import static com.makaan.constants.RequestConstants.LISTING_AGGREGATIONS;
import static com.makaan.constants.RequestConstants.LOCALITY_HEROSHOT_IMAGE_URL;
import static com.makaan.constants.RequestConstants.LOCALITY_ID;
import static com.makaan.constants.RequestConstants.LONGITUDE;
import static com.makaan.constants.RequestConstants.SORT_ASC;
import static com.makaan.constants.RequestConstants.SUBURB;

/**
 * Created by rohitgarg on 2/8/16.
 */
public class LocationService implements MakaanService {
    public void getUserLocation() {

        /*Selector selector = new Selector();
        selector.field("id").field("label").field("centerLatitude").field("centerLongitude");
        MakaanNetworkClient.getInstance().get(ApiConstants.MY_LOCATION.concat("?").concat(selector.build()).concat("&format=json"), new JSONGetCallback() {*/
        MakaanNetworkClient.getInstance().get(ApiConstants.MY_LOCATION.concat("?format=json"), new JSONGetCallback() {
            @Override
            public void onError(ResponseError error) {

                LocationGetEvent locationGetEvent = new LocationGetEvent();
                locationGetEvent.error = error;
                AppBus.getInstance().post(locationGetEvent);
            }

            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                        Type locationType = new TypeToken<MyLocation>() {}.getType();
                        MyLocation location = MakaanBuyerApplication.gson.fromJson(data.getJSONObject(ResponseConstants.CITY).toString(), locationType);
                        AppBus.getInstance().post(new LocationGetEvent(location));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getTopLocalitiesAsSearchResult(double lat, double lon, final long id) {
        Selector selector = new Selector();
        selector.field(LOCALITY_ID).field(CITY_ID).field(LABEL).field(LATITUDE).field(LONGITUDE).field(SUBURB).field(CITY);
        selector.page(0, 5);
        selector.sort("localityLivabilityScore", "DESC");
        if(id > 0) {
            selector.term("cityId", String.valueOf(id));
        } else if(lat > 0 && lon > 0) {
            selector.nearby(RequestConstants.GEO_REQUEST_DISTANCE, lat, lon, false);
        }

        MakaanNetworkClient.getInstance().get(ApiConstants.LOCALITY_DATA.concat("?").concat(selector.build()).concat("&format=json"), new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {
                SearchResultEvent searchResultEvent = new SearchResultEvent();
                searchResultEvent.error = error;
                AppBus.getInstance().post(searchResultEvent);
            }

            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONArray array = responseObject.getJSONArray(ResponseConstants.DATA);
                        ArrayList<SearchResponseItem> items = new ArrayList<SearchResponseItem>();
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            if(obj != null) {
                                SearchResponseItem item = new SearchResponseItem();
                                item.cityId = obj.getLong(CITY_ID);
                                item.entityId = String.valueOf(obj.getLong(LOCALITY_ID));
                                item.displayText = obj.getString(LABEL);
                                item.entityName = obj.getString(LABEL);
                                if(id > 0 && item.cityId == id && Session.apiLocation != null) {
                                    item.city = Session.apiLocation.label;
                                }
                                item.type = SearchSuggestionType.LOCALITY.getValue();
                                item.id = String.format("TYPEAHEAD-LOCALITY-%s", item.entityId);
                                item.latitude = obj.getDouble(LATITUDE);
                                item.longitude = obj.getDouble(LONGITUDE);

                                if(obj.has(SUBURB)) {
                                    JSONObject obj1 = obj.getJSONObject(SUBURB);
                                    if(obj1 != null && obj1.has(CITY)) {
                                        obj1 = obj1.getJSONObject(CITY);
                                        if(obj1 != null && obj1.has(LABEL)) {
                                            item.city = obj1.getString(LABEL);
                                        }
                                    }
                                }

                                items.add(item);
                            }
                        }

                        SearchResponseItem item = new SearchResponseItem();
                        item.type = SearchSuggestionType.HEADER_TEXT.getValue();
                        item.displayText = "top localities";
                        items.add(0, item);

                        SearchResponse response = new SearchResponse();
                        response.setTotalCount(items.size());
                        response.setData(items);
                        SearchResultEvent event = new SearchResultEvent();
                        event.searchResponse = response;
                        AppBus.getInstance().post(event);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getTopNearbyLocalitiesAsSearchResult(SearchResponseItem item) {
        Selector selector = new Selector();
        selector.field(LOCALITY_ID).field(CITY_ID).field(LABEL).field(LATITUDE).field(LONGITUDE).field(SUBURB).field(CITY);
        selector.page(0, 5);
        selector.sort("geoDistance", "ASC");
        selector.nearby(10, item.latitude, item.longitude, false);
        MakaanNetworkClient.getInstance().get(ApiConstants.LOCALITY_DATA.concat("?").concat(selector.build()).concat("&format=json"), new JSONGetCallback() {

            @Override
            public void onError(ResponseError error) {
                SearchResultEvent searchResultEvent = new SearchResultEvent();
                searchResultEvent.error = error;
                AppBus.getInstance().post(searchResultEvent);
            }

            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    JSONArray array = responseObject.getJSONArray(ResponseConstants.DATA);
                    ArrayList<SearchResponseItem> items = new ArrayList<SearchResponseItem>();
                    for(int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if(obj != null) {
                            SearchResponseItem item = new SearchResponseItem();
                            item.cityId = obj.getLong(CITY_ID);
                            item.entityId = String.valueOf(obj.getLong(LOCALITY_ID));
                            item.displayText = obj.getString(LABEL);
                            item.entityName = obj.getString(LABEL);
                            /*if(item.cityId == apiLocation.id) {
                                item.city = apiLocation.label;
                            }*/
                            item.type = SearchSuggestionType.LOCALITY.getValue();
                            item.id = String.format("TYPEAHEAD-LOCALITY-%s", item.entityId);
                            item.latitude = obj.getDouble(LATITUDE);
                            item.longitude = obj.getDouble(LONGITUDE);

                            if(obj.has(SUBURB)) {
                                JSONObject obj1 = obj.getJSONObject(SUBURB);
                                if(obj1 != null && obj1.has(CITY)) {
                                    obj1 = obj1.getJSONObject(CITY);
                                    if(obj1 != null && obj1.has(LABEL)) {
                                        item.city = obj1.getString(LABEL);
                                    }
                                }
                            }

                            items.add(item);
                        }
                    }

                    SearchResponseItem item = new SearchResponseItem();
                    item.type = SearchSuggestionType.HEADER_TEXT.getValue();
                    item.displayText = "nearby localities";
                    items.add(0, item);

                    SearchResponse response = new SearchResponse();
                    response.setTotalCount(items.size());
                    response.setData(items);
                    SearchResultEvent event = new SearchResultEvent();
                    event.searchResponse = response;
                    AppBus.getInstance().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
