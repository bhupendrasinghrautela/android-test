package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.makaan.constants.RequestConstants.FROM;
import static com.makaan.constants.RequestConstants.RANGE;
import static com.makaan.constants.RequestConstants.TO;

/**
 * Created by vaibhav on 08/01/16.
 */
public class RangeSelector implements MakaanReqSelector {


    public String fieldName;
    public Double from;
    public Double to;

    public RangeSelector(String fieldName, Double from, Double to) {
        this.fieldName = fieldName;
        this.from = from;
        this.to = to;
    }


    public RangeSelector() {

    }

    @Override
    public String build() {

        if (null == fieldName || (null == from && null == to)) {
            return "";
        }

        LinkedHashMap<String, Double> fromToMap = new LinkedHashMap<>();
        if (null != from) {
            fromToMap.put(FROM, from);
        }
        if (null != to) {
            fromToMap.put(TO, to);
        }

        LinkedHashMap<String, Map<String, Double>> fieldMap = new LinkedHashMap<>();
        fieldMap.put(fieldName, fromToMap);

        LinkedHashMap<String, Map<String, Map<String, Double>>> jsonMap = new LinkedHashMap<>();

        jsonMap.put(RANGE, fieldMap);

        String s = MakaanBuyerApplication.gson.toJson(jsonMap);

        return s;


    }


}
