package com.makaan.response.trend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by vaibhav on 11/01/16.
 */
public class LocalityPriceTrendDto {


    public HashMap<PriceTrendKey, List<PriceTrendData>> data = new LinkedHashMap<>();

    private HashMap<Long, PriceTrendKey> localityIdToTrendKey = new HashMap<>();


    public void addPriceTrendData(Long entityId, String entityName, Date dataDate, Long minPricePerUnitArea){

        PriceTrendKey priceTrendKey = localityIdToTrendKey.get(entityId);

        if(null == priceTrendKey){
            priceTrendKey = new PriceTrendKey(entityId, entityName);
            localityIdToTrendKey.put(entityId, priceTrendKey);
        }
        List<PriceTrendData> trendDataList = data.get(priceTrendKey);
        if(null == trendDataList){
            trendDataList = new ArrayList<>();
            data.put(priceTrendKey, trendDataList);
        }
        trendDataList.add(new PriceTrendData(dataDate, minPricePerUnitArea));

    }


}