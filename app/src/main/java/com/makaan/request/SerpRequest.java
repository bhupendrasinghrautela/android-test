package com.makaan.request;

import java.util.List;
import java.util.Map;

/**
 * Created by vaibhav on 04/01/16.
 */
public class SerpRequest {


    public long cityId;
    public long localityId;
    public long landMarkId;




    public SerpRequest applyTermFilter(String fieldName, String value){

        return this;
    }

    public SerpRequest applyRangeFilter(String fieldName, Double from , Double to){
        return this;
    }




    private Map<String, List<Map<String, Map<String, Object>>>> filters;



    private void buildSelector(){

    }
}
