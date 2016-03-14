package com.makaan.service;

import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.buyerjourney.NewMatchesGetEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.network.JSONGetCallback;
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
import java.util.HashMap;

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

                MasterDataCache.getInstance().clearSavedSearches();
                for (SaveSearch savedSearch : saveSearchList) {
                    if (null != savedSearch) {
                        MasterDataCache.getInstance().addSavedSearch(savedSearch);
                    }
                }

                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearchList));
            }
        }, true);
    }

    /**
     * http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches/new-matches
     **/
    public void getSavedSearchesNewMatches() {
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_NEW_MATCHES_URL;

        MakaanNetworkClient.getInstance().get(getSavedSearchUrl, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                try {
                    int data = responseObject.getInt(ResponseConstants.DATA);
                    NewMatchesGetEvent event = new NewMatchesGetEvent();
                    event.totalCount = data;
                    AppBus.getInstance().post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                    NewMatchesGetEvent event = new NewMatchesGetEvent();
                    ResponseError error = new ResponseError();
                    error.error = new VolleyError(e);
                    event.error = error;
                    AppBus.getInstance().post(event);
                }
            }

            @Override
            public void onError(ResponseError error) {
                NewMatchesGetEvent event = new NewMatchesGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }
    public void getSavedSearchesNewMatchesByIds(ArrayList<Long> ids) {
        String getSavedSearchUrl = ApiConstants.SAVED_SEARCH_NEW_MATCHES_URL;
        if(ids != null && ids.size() > 0) {
            String separator = "";
            getSavedSearchUrl = getSavedSearchUrl.concat("?savedSearchId=");
            for(Long id : ids) {
                getSavedSearchUrl = getSavedSearchUrl.concat(separator);
                getSavedSearchUrl = getSavedSearchUrl.concat(String.valueOf(id));
                separator = ",";
            }
        }
        Type saveSearchType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(getSavedSearchUrl, saveSearchType, new ObjectGetCallback() {
            @Override
            public void onSuccess(Object responseObject) {
                NewMatchesGetEvent event = new NewMatchesGetEvent();
                if(responseObject != null && responseObject instanceof HashMap) {
                    event.data = (HashMap) responseObject;
                }
                AppBus.getInstance().post(event);
            }

            @Override
            public void onError(ResponseError error) {
                NewMatchesGetEvent event = new NewMatchesGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }

    /**
     * http:/marketplace-qa.makaan-ws.com/data/v1/entity/user/saved-searches/<savedSearchId>
     * Posts list of remaining saved searches
     **/
    public void removeSavedSearch(Long savedSearchId) {
        if(savedSearchId == null) {
            return;
        }
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
                MasterDataCache.getInstance().clearSavedSearches();
                for (SaveSearch savedSearch : saveSearch) {
                    if (null != savedSearch) {
                        MasterDataCache.getInstance().addSavedSearch(savedSearch);
                    }
                }
                AppBus.getInstance().post(new SaveSearchGetEvent(saveSearch));
            }

        }, TAG, true);
    }

    public void saveNewSearch(Selector selector) {
        saveNewSearch(selector, null);
    }

    public void saveNewSearch(Selector selector, String name) {

        final SaveNewSearch saveNewSearch = new SaveNewSearch();
        saveNewSearch.searchQuery = selector.build();
        if(saveNewSearch.searchQuery != null && saveNewSearch.searchQuery.contains("selector=")) {
            saveNewSearch.searchQuery = saveNewSearch.searchQuery.replace("selector=", "");
        }
        if (TextUtils.isEmpty(name)) {
            saveNewSearch.name = selector.getUniqueName();
        } else {
            saveNewSearch.name = name;
        }
        saveNewSearch(saveNewSearch);
    }

    public void saveNewSearch(SaveNewSearch saveNewSearch) {
        String saveNewSearchUrl = ApiConstants.SAVE_NEW_SEARCH_URL;
        Type saveSearchType = new TypeToken<SaveSearch>() {
        }.getType();

        try {
            JSONObject jsonObject = JsonBuilder.toJson(saveNewSearch);
            MakaanNetworkClient.getInstance().post(saveNewSearchUrl, saveSearchType, jsonObject, new ObjectGetCallback() {

                @Override
                public void onSuccess(Object responseObject) {
                    if(responseObject != null) {
                        SaveSearchGetEvent event = new SaveSearchGetEvent();
                        event.saveSearchArrayList = new ArrayList<>();
                        event.saveSearchArrayList.add((SaveSearch) responseObject);
                        AppBus.getInstance().post(event);
                    } else {
                        AppBus.getInstance().post(null);
                    }
                }

                @Override
                public void onError(ResponseError error) {
                    //TODO handle error
                    SaveSearchGetEvent event = new SaveSearchGetEvent();
                    event.error = error;
                    AppBus.getInstance().post(event);
                }
            }, TAG, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveNewSearch(Selector selector, String name, String email) {
        String saveNewSearchUrl = ApiConstants.SAVE_NEW_SEARCH_URL;
        Type saveSearchType = new TypeToken<SaveSearch>() {
        }.getType();

        if(!TextUtils.isEmpty(email)) {
            saveNewSearchUrl = saveNewSearchUrl.concat("?email=" + email);
        }

        SaveNewSearch saveNewSearch = new SaveNewSearch();
        saveNewSearch.searchQuery = selector.build();
        if(saveNewSearch.searchQuery != null && saveNewSearch.searchQuery.contains("selector=")) {
            saveNewSearch.searchQuery = saveNewSearch.searchQuery.replace("selector=", "");
        }
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
                    ArrayList<SaveSearch> arrayList=new ArrayList<SaveSearch>();
                    arrayList.add(saveSearch);
                    AppBus.getInstance().post(new SaveSearchGetEvent(arrayList));
                }

                @Override
                public void onError(ResponseError error) {
                    SaveSearchGetEvent saveSearchGetEvent = new SaveSearchGetEvent();
                    saveSearchGetEvent.error = error;
                    AppBus.getInstance().post(saveSearchGetEvent);
                    //TODO handle error
                }
            }, TAG, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
