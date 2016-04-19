package com.makaan.service;

import android.text.TextUtils;

import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.search.SearchResultCallback;
import com.makaan.response.search.SearchType;
import com.makaan.util.CommonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sunil on 07/01/16.
 *
 * Network call service for making search request
 */
public class SearchService implements MakaanService {
    private static final String TAG = SearchService.class.getSimpleName();

    /*---Typeahead api constants*/
    public static final String TYPEAHEAD_BASE_URL = ApiConstants.TYPEAHEAD;
    public static final String TYPEAHEAD_QUERY = "query=";
    public static final String TYPEAHEAD_TYPE = "typeAheadType=";
    public static final String TYPEAHEAD_ROWS = "&rows=5";
    public static final String TYPEAHEAD_ENHANCE_GP = "&enhance=gp";
    public static final String TYPEAHEAD_CITY = "&city=";
    public static final String TYPEAHEAD_CATEGORY = "&category=";
    public static final String TYPEAHEAD_BUYER = "&view=buyer";
    public static final String TYPEAHEAD_FORMAT = "&format=json";


    /**
     * Gets search results
     * @param keyword search key
     * @param category search category buy/rent
     * @param city search city
     * @param type search result type
     * @param supportGooglePlace are google places results required    */
    public void getSearchResults(String keyword, String category, String city, SearchType type, boolean supportGooglePlace)
            throws UnsupportedEncodingException {

        getSearchResults(keyword, category, city, new SearchType[] {type}, supportGooglePlace);
    }

    /**
     * Gets search results
     * @param keyword search key
     * @param category search category buy/rent
     * @param city search city
     * @param type search result type
     * @param supportGooglePlace are google places results required    */
    public void getSearchResults(String keyword, String category, String city, SearchType[] type, boolean supportGooglePlace)
            throws UnsupportedEncodingException {

        if(TextUtils.isEmpty(keyword)){
            return;
        } else {

            String mSearchString = keyword.trim();
            String requestUrl = buildSearchUrl(mSearchString, category, city, type, supportGooglePlace);
            makeSearchRequest(requestUrl);
        }
    }

    private void makeSearchRequest(String requestUrl) {
        MakaanNetworkClient.getInstance().getSearch(requestUrl, new SearchResultCallback(), TAG);
    }

    private String buildSearchUrl(String key, String category, String city, SearchType[] type, boolean supportGooglePlace){
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(TYPEAHEAD_BASE_URL);

        try {
            key = URLEncoder.encode(key, "utf-8");
        } catch (UnsupportedEncodingException e) {
            CommonUtil.TLog("exception", e);
        }

        if(type.length == 1 && type[0] == SearchType.ALL) {
            urlBuilder.append(TYPEAHEAD_QUERY);
            urlBuilder.append(key);
            if(supportGooglePlace){
                urlBuilder.append(TYPEAHEAD_ENHANCE_GP);
            }

        } else {
            urlBuilder.append(TYPEAHEAD_TYPE);
            String prefix = "";
            for(SearchType searchType : type) {
                urlBuilder.append(prefix);
                prefix = ",";
                urlBuilder.append(searchType.getValue());
            }

            urlBuilder.append("&");
            urlBuilder.append(TYPEAHEAD_QUERY);
            urlBuilder.append(key);
        }

        if(!TextUtils.isEmpty(city)) {
            urlBuilder.append(TYPEAHEAD_CITY);
            urlBuilder.append(city);
        }

        if(!TextUtils.isEmpty(category)) {
            urlBuilder.append(TYPEAHEAD_CATEGORY);
            urlBuilder.append(category);
        }
        urlBuilder.append(TYPEAHEAD_BUYER);
        urlBuilder.append(TYPEAHEAD_FORMAT);

        urlBuilder.append(TYPEAHEAD_ROWS);
        return urlBuilder.toString();
    }
}
