package com.makaan.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 04/01/16.
 */
public class SerpRequest {


    public long cityId;
    public long localityId;
    public long landMarkId;

    Map<String, List<String>> termFilterValues = new HashMap<>();



    public SerpRequest applyTermFilter(String fieldName, String value){
        List<String> values = termFilterValues.get(fieldName);
        if(null == values){
            values = new ArrayList<>();
            termFilterValues.put(fieldName, values);
        }
        values.add(value);

        return this;
    }

    public SerpRequest applyRangeFilter(String fieldName, Double from , Double to){
        return this;
    }




    private Map<String, Map<String, String>> selector;



    private void buildSelector(){

        List<Map<String, Map<String, Object>>> filtersValues = new ArrayList<>();


        for(Map.Entry<String, List<String>> termFilterEntry : termFilterValues.entrySet()){
            //filtersValues.
        }

        //selector.put(FILTERS, )

        //selector.put(PAGING, )

    }
}
