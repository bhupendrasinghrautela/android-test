package com.makaan.service.search;

import android.text.TextUtils;

import com.makaan.constants.ApiConstants;
import com.makaan.constants.StringConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.search.SearchResultCallback;
import com.makaan.response.search.SearchType;
import com.makaan.service.MakaanService;
import com.makaan.util.StringUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by sunil on 07/01/16.
 *
 * Network call service for making search request
 */
public class SearchService implements MakaanService {
    private static final String TAG = SearchService.class.getSimpleName();


    /**
     * Gets search results
     * @param keyword search key
     * @param city search city
     * @param type search result type
     * @param supportGooglePlace are google places results required
     * */
    public void getSearchResults(String keyword, String city, SearchType type, boolean supportGooglePlace)
            throws UnsupportedEncodingException {

        if(TextUtils.isEmpty(keyword)){
            throw new IllegalArgumentException("Keyword cannot be empty");
        } else {

            String mSearchString = keyword.trim();
            if (!StringUtil.isValidKeyword(mSearchString,
                    StringConstants.REGEX_TYPEAHEAD_ALLOWED_CHARS)) {
                throw new IllegalArgumentException("Invalid keyword");
            }

            String requestUrl = buildSearchUrl(keyword, city, type, supportGooglePlace);
            makeSearchRequest(requestUrl);
        }
    }

    private void makeSearchRequest(String requestUrl) {
        MakaanNetworkClient.getInstance().cancelFromRequestQueue(TAG);
        MakaanNetworkClient.getInstance().get(requestUrl, new SearchResultCallback(), TAG);
    }

    private String buildSearchUrl(String key, String city, SearchType type, boolean supportGooglePlace){
            StringBuilder urlBuilder = new StringBuilder();
            if (type == SearchType.ALL) {
                urlBuilder.append(ApiConstants.TYPEAHEAD_BASE_URL);
                urlBuilder.append(ApiConstants.TYPEAHEAD_QUERY);
                urlBuilder.append(key);
                if(supportGooglePlace){
                    urlBuilder.append(ApiConstants.TYPEAHEAD_ENHANCE_GP);
                }
                urlBuilder.append(ApiConstants.TYPEAHEAD_CITY);
                urlBuilder.append(city);
                urlBuilder.append(ApiConstants.TYPEAHEAD_ROWS);
            } else {
                urlBuilder.append(ApiConstants.TYPEAHEAD_BASE_URL);
                urlBuilder.append(ApiConstants.TYPEAHEAD_TYPE);
                urlBuilder.append(type.getValue());
                urlBuilder.append("&");
                urlBuilder.append(ApiConstants.TYPEAHEAD_QUERY);
                urlBuilder.append(key);
                urlBuilder.append(ApiConstants.TYPEAHEAD_CITY);
                urlBuilder.append(city);
            }
            return urlBuilder.toString();
    }
}
