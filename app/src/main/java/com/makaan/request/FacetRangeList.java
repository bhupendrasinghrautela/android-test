package com.makaan.request;

import com.makaan.MakaanBuyerApplication;

import java.util.ArrayList;

/**
 * Created by vaibhav on 13/01/16.
 */
public class FacetRangeList {


    public ArrayList<FacetRange> facetRanges = new ArrayList<>();


    public String toJson() {
        return MakaanBuyerApplication.gson.toJson(facetRanges);
    }
}
