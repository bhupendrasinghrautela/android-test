package com.makaan.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.response.search.SearchResponseItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rohitgarg on 1/19/16.
 */
public class RecentSearchManager {
    private static final String SHARED_PREF_KEY = "recent_search";
    private static final String PREF_KEY = "key";
    private static final int MAX_ENTRIES = 5;

    ArrayList<SearchResponseItem> recentSearchList = new ArrayList<SearchResponseItem>(MAX_ENTRIES);

    private static RecentSearchManager manager;

    public static RecentSearchManager getInstance(Context context) {
        if(manager == null) {
            synchronized (RecentSearchManager.class) {
                if(manager == null) {
                    manager = new RecentSearchManager(context);
                }
            }
        }
        return manager;
    }

    public RecentSearchManager(Context context) {
        loadSearchEntries(context);
    }

    private void loadSearchEntries(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        String searchJsonString = Preference.getString(preferences, PREF_KEY);

        if(searchJsonString != null) {
            Type apiPriceTrendType = new TypeToken<ArrayList<SearchResponseItem>>() {}.getType();
            recentSearchList = MakaanBuyerApplication.gson.fromJson(searchJsonString, apiPriceTrendType);
        }
    }

    public void addEntryToRecentSearch(SearchResponseItem search, Context context) {
        for (Iterator<SearchResponseItem> iterator = recentSearchList.iterator(); iterator.hasNext();) {
            SearchResponseItem s = iterator.next();
            if (s.id.equals(search.id)) {
                iterator.remove();
            }
        }
        recentSearchList.add(0, search);
        if(recentSearchList.size() > 5) {
            recentSearchList.remove(5);
        }

        String searchJsonString = MakaanBuyerApplication.gson.toJson(recentSearchList);

        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        Preference.putString(edit, PREF_KEY, searchJsonString);
        edit.apply();
    }

    public ArrayList<SearchResponseItem> getRecentSearches(Context context) {
        return recentSearchList;
    }
}
