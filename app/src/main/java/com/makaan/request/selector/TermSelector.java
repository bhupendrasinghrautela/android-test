package com.makaan.request.selector;

import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.RequestConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by vaibhav on 08/01/16.
 */
public class TermSelector implements MakaanReqSelector{

    public String name;

    public HashSet<String> values = new HashSet<>();


    public TermSelector(String name, String value) {
        this.name = name;
        this.values.add(value);
    }

    public TermSelector() {

    }

    public TermSelector(TermSelector selector) {
        this.name = selector.name;
        this.values.addAll(selector.values);
    }

    @Override
    public String build(){

        if(null == name){
            return "";
        }

        Map<String, Map<String, HashSet<String>>> jsonMap = new HashMap<>();

        Map<String, HashSet<String>> value = new HashMap<>();
        value.put(name, values);

        jsonMap.put(RequestConstants.EQUAL, value);

        return MakaanBuyerApplication.gson.toJson(jsonMap);
    }



}
