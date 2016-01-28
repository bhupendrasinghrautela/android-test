package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;

import java.util.LinkedHashMap;

/**
 * Created by vaibhav on 25/01/16.
 */
public class GroupBySelector implements MakaanReqSelector {


    public String field;
    public String min;

    public GroupBySelector(String field, String min) {
        this.field = field;
        this.min = min;
    }

    public GroupBySelector() {
    }


    @Override
    public String build() {

        if (null == field || null == min) {
            return "";
        }

        LinkedHashMap<String, String> groupByMap = new LinkedHashMap<>();

        groupByMap.put("field", field);
        groupByMap.put("min", min);


        String s = MakaanBuyerApplication.gson.toJson(groupByMap);
        return s;
    }
}
