package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.makaan.constants.RequestConstants.DISTANCE;
import static com.makaan.constants.RequestConstants.LAT;
import static com.makaan.constants.RequestConstants.LON;
import static com.makaan.constants.RequestConstants.RANGE;
import static com.makaan.constants.RequestConstants.GEO;
import static com.makaan.constants.RequestConstants.GEO_DISTANCE;

/**
 * Created by sunil on 10/01/16.
 */
public class GeoSelector implements MakaanReqSelector {

    public Double distance;
    public Double lat;
    public Double lon;

    @Override
    public String build() {

        if(null == distance || null == lat || null == lon){
            return "";
        }

        LinkedHashMap<String, Double> fromToMap = new LinkedHashMap<>();
        fromToMap.put(DISTANCE, distance);
        fromToMap.put(LAT, lat);
        fromToMap.put(LON, lon);

        LinkedHashMap<String, Map<String, Double>> fieldMap = new LinkedHashMap<>();
        fieldMap.put(GEO, fromToMap);

        LinkedHashMap<String, Map<String, Map<String, Double>>> jsonMap = new LinkedHashMap<>();

        jsonMap.put(GEO_DISTANCE, fieldMap);

        String s =  MakaanBuyerApplication.gson.toJson(jsonMap);

        return s;

    }
}
