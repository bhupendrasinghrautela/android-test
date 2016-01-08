package com.makaan.response.search;

import com.makaan.network.JSONGetCallback;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 07/01/16.
 */
public class SearchResultCallback extends StringRequestCallback {

    @Override
    public void onSuccess(String response) {

        SearchResultEvent searchResultEvent = new SearchResultEvent();
        SearchResponse searchResponse =
                (SearchResponse) JsonParser.parseJson(
                        response, SearchResponse.class);

        if(searchResponse == null || searchResponse.getData() == null ) {

            ResponseError error = new ResponseError();
            error.setMsg("Data null");
            searchResultEvent.error = error;

        }else {
            //TODO populate UI field for project id

            searchResultEvent.searchResponse = searchResponse;
        }

        AppBus.getInstance().post(searchResultEvent);
    }
}
