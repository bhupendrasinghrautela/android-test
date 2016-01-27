package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.saveSearch.SaveNewSearch;
import com.makaan.request.selector.Selector;
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
     *  http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches
     **/
    public void getSavedSearches(){
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_URL;
        Type saveSearchType = new TypeToken<ArrayList<SaveSearch>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {
            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<SaveSearch> saveSearchList = (ArrayList<SaveSearch>) responseObject;
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearchList));
            }
        },true);
    }

    /**
     *  http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches/<savedSearchId>
     *  Posts list of remaining saved searches
     **/
    public void removeSavedSearch(Long savedSearchId){
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_URL.concat(savedSearchId.toString());
        Type saveSearchType = new TypeToken<ArrayList<SaveSearch>>() {
        }.getType();

        MakaanNetworkClient.getInstance().delete(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {

            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<SaveSearch> saveSearch = (ArrayList<SaveSearch>) responseObject;
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearch));
            }

            @Override
            protected void onError() {
                super.onError();
            }
        }, TAG,true);
    }

    public void saveNewSearch(Selector selector){
        String saveNewSearchUrl = ApiConstants.SAVE_NEW_SEARCH_URL;
        Type saveSearchType = new TypeToken<SaveSearch>() {
        }.getType();

        SaveNewSearch saveNewSearch = new SaveNewSearch();
        saveNewSearch.searchQuery = selector.build();
        saveNewSearch.name=selector.getUniqueName();

        try {
            JSONObject jsonObject= JsonBuilder.toJson(saveNewSearch);
            MakaanNetworkClient.getInstance().post(saveNewSearchUrl, saveSearchType, jsonObject, new ObjectGetCallback() {

                @Override
                public void onSuccess(Object responseObject) {
                    SaveSearch saveSearch = (SaveSearch) responseObject;
                }

                @Override
                protected void onError() {
                    super.onError();
                }
            }, TAG, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
