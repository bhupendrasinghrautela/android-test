package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 08/01/16.
 */
public class SortSelector implements MakaanReqSelector {

    public String fieldName;
    public String sortOrder;

    public SortSelector(String fieldName, String sortOrder) {
        this.fieldName = fieldName;
        this.sortOrder = sortOrder;
    }

    public SortSelector() {
    }

    @Override
    public String build() {

        if (null == fieldName || null == sortOrder) {
            return "";
        }

        ArrayList<Map<String, String>> jsonArray = new ArrayList<>();

        LinkedHashMap<String, String> sortMap = new LinkedHashMap<>();
        sortMap.put(FIELD, fieldName);
        sortMap.put(SORT_ORDER, sortOrder.toUpperCase());

        jsonArray.add(sortMap);

        return MakaanBuyerApplication.gson.toJson(jsonArray);
    }
}
