package com.makaan.response.search;

import com.makaan.response.BaseResponse;

import java.util.ArrayList;

/**
 * A model for typeahead getSearchResult response
 * 
 * */
public class SearchResponse extends BaseResponse {

    private int totalCount;
    private ArrayList<SearchResponseItem> data;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<SearchResponseItem> getData() {
        return data;
    }

    public void setData(ArrayList<SearchResponseItem> data) {
        this.data = data;
    }
}
