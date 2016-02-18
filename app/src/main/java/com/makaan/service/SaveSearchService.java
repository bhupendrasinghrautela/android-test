package com.makaan.service;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.saveSearch.SaveNewSearch;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.util.AppBus;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by aishwarya on 26/01/16.
 */
public class SaveSearchService implements MakaanService {

    public final String TAG = SaveSearchService.class.getSimpleName();

    /**
     * http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches
     **/
    public void getSavedSearches() {
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_URL;
        Type saveSearchType = new TypeToken<ArrayList<SaveSearch>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {
                SaveSearchGetEvent saveSearchGetEvent = new SaveSearchGetEvent();
                saveSearchGetEvent.error = error;
                AppBus.getInstance().post(saveSearchGetEvent);
            }

            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<SaveSearch> saveSearchList = (ArrayList<SaveSearch>) responseObject;
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearchList));
            }
        }, true);
    }

    /**
     * http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches/new-matches
     **/
    public void getSavedSearchesNewMatches() {
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_NEW_MATCHES_URL;
        // TODO
        /*Type saveSearchType = new TypeToken<ArrayList<SaveSearch>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {
                SaveSearchGetEvent saveSearchGetEvent = new SaveSearchGetEvent();
                saveSearchGetEvent.error = error;
                AppBus.getInstance().post(saveSearchGetEvent);
            }

            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<SaveSearch> saveSearchList = (ArrayList<SaveSearch>) responseObject;
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearchList));
            }
        }, true);*/
    }

    /**
     * http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches/<savedSearchId>
     * Posts list of remaining saved searches
     **/
    public void removeSavedSearch(Long savedSearchId) {
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_URL.concat(savedSearchId.toString());
        Type saveSearchType = new TypeToken<ArrayList<SaveSearch>>() {
        }.getType();

        MakaanNetworkClient.getInstance().delete(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {

            @Override
            public void onError(ResponseError error) {
                SaveSearchGetEvent saveSearchGetEvent = new SaveSearchGetEvent();
                saveSearchGetEvent.error = error;
                AppBus.getInstance().post(saveSearchGetEvent);
            }

            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<SaveSearch> saveSearch = (ArrayList<SaveSearch>) responseObject;
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearch));
            }

        }, TAG, true);
    }

    public void saveNewSearch(Selector selector) {
        saveNewSearch(selector, null);
    }

    public void saveNewSearch(Selector selector, String name) {
        String saveNewSearchUrl = ApiConstants.SAVE_NEW_SEARCH_URL;
        Type saveSearchType = new TypeToken<SaveSearch>() {
        }.getType();

        SaveNewSearch saveNewSearch = new SaveNewSearch();
        saveNewSearch.searchQuery = selector.build();
        if (TextUtils.isEmpty(name)) {
            saveNewSearch.name = selector.getUniqueName();
        } else {
            saveNewSearch.name = name;
        }

        try {
            JSONObject jsonObject = JsonBuilder.toJson(saveNewSearch);
            MakaanNetworkClient.getInstance().post(saveNewSearchUrl, saveSearchType, jsonObject, new ObjectGetCallback() {

                @Override
                public void onSuccess(Object responseObject) {
                    SaveSearch saveSearch = (SaveSearch) responseObject;
                }

                @Override
                public void onError(ResponseError error) {
                    //TODO handle error
                }
            }, TAG, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
