package com.makaan.request.selector;

import android.util.Log;

import com.makaan.MakaanBuyerApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.makaan.constants.RequestConstants.*;
import static com.makaan.constants.RequestConstants.SORT;

/**
 * Created by vaibhav on 08/01/16.
 */
public class Selector {

    public static final String TAG = Selector.class.getSimpleName();

    private LinkedHashMap<String, TermSelector> termSelectorHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, RangeSelector> rangeSelectorHashMap = new LinkedHashMap<>();
    private PagingSelector pagingSelector = new PagingSelector();
    private SortSelector sortSelector = new SortSelector();

    public Selector term(String fieldName, Iterable<String> values) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);

        if (null == termSelector) {
            termSelector = new TermSelector();
            termSelectorHashMap.put(fieldName, termSelector);

        }
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            termSelector.values.add(iterator.next());
        }

        return this;
    }

    public Selector term(String fieldName, String value) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);
        if (null == termSelector) {
            termSelector = new TermSelector(fieldName, value);
            termSelectorHashMap.put(fieldName, termSelector);

        } else {
            termSelector.values.add(value);
        }

        return this;
    }

    public Selector range(String fieldName, Double from, Double to) {
        RangeSelector rangeSelector = rangeSelectorHashMap.get(fieldName);
        if (null == rangeSelector) {
            rangeSelector = new RangeSelector(fieldName, from, to);
            rangeSelectorHashMap.put(fieldName, rangeSelector);
        } else {
            rangeSelector.from = from;
            rangeSelector.to = to;
        }

        return this;
    }

    public Selector page(int start, int rows) {
        pagingSelector.start = start;
        pagingSelector.rows = rows;
        return this;
    }

    public Selector sort(String sortField, String sortOrder) {
        sortSelector.fieldName = sortField;
        sortSelector.sortOrder = sortOrder;
        return this;
    }


    public String build() {
        try {


            StringBuilder andStrBuilder = new StringBuilder();
            andStrBuilder.append(AND).append(":[");

            int i =0;
            for (LinkedHashMap.Entry<String, TermSelector> entry : termSelectorHashMap.entrySet()) {

                String k = entry.getValue().build();
                if(null != k  && !"".equals(k)){
                    if(i !=0){
                        andStrBuilder.append(",").append(k);
                    }else {
                        andStrBuilder.append(k);
                    }
                    i++;
                }
            }

            i =0;
            for (LinkedHashMap.Entry<String, RangeSelector> entry : rangeSelectorHashMap.entrySet()) {

                String k = entry.getValue().build();
                if(null != k  && !"".equals(k)){
                    if(i !=0){
                        andStrBuilder.append(",").append(k);
                    }else {
                        andStrBuilder.append(k);
                    }
                    i++;
                }
            }

           andStrBuilder.append("]");





            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{").append("\"").append(FILTERS).append("\"").append(":{").append(andStrBuilder.toString()).append(",").
                    append("\"").append(PAGING).append("\"").append(":").append(pagingSelector.build())
                    .append(",").append("\"").append(SORT).append("\"").append(":").append(sortSelector.build()).append("}}");


            String s = jsonBuilder.toString();

            s = SELECTOR.concat("=").concat(s);
            //String s = SELECTOR.concat("=").concat(jsonObject.toString());

           // s = URLEncoder.encode(s, "utf-8");
            return s;

        } catch (Exception e) {
            Log.e(TAG, "Error while building Selector", e);
        }


        return null;
    }


    public static void main(String[] args) {


        Selector selector = new Selector();

        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("2");
        cityList.add("3");
        selector.term("cityId", cityList).term("unitType", "apartment").range("price", 0D, 50000D).page(0, 5);


        System.out.println(selector.build());
    }

}
