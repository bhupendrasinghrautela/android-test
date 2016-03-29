package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;

import java.util.LinkedHashMap;

import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 08/01/16.
 */
public class PagingSelector implements MakaanReqSelector {

    public Integer start;
    public Integer rows;


    public PagingSelector(Integer start, Integer rows) {
        this.start = start;
        this.rows = rows;
    }

    public PagingSelector() {
    }

    @Override
    public String build() {

        if (null == start || null == rows) {
            return "";
        }

        LinkedHashMap<String, String> paginationMap = new LinkedHashMap<>();

        paginationMap.put(START, start.toString());
        paginationMap.put(ROWS, rows.toString());


        String s = MakaanBuyerApplication.gson.toJson(paginationMap);
        return s;
    }
}
