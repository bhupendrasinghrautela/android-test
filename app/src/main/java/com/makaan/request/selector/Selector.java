package com.makaan.request.selector;

import android.util.Log;

import com.makaan.MakaanBuyerApplication;
import com.makaan.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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


    private HashSet<String> fieldSelector = new HashSet<>();
    private LinkedHashMap<String, TermSelector> termSelectorHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, RangeSelector> rangeSelectorHashMap = new LinkedHashMap<>();
    private PagingSelector pagingSelector = new PagingSelector();
    private SortSelector sortSelector = new SortSelector();

    public Selector fields(String[] fields) {
        for (String field : fields) {
            fieldSelector.add(field);
        }
        return this;
    }

    public Selector field(String fieldName) {
        fieldSelector.add(fieldName);
        return this;
    }

    public Selector term(String fieldName, Iterable<String> values) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);

        if (null == termSelector) {
            termSelector = new TermSelector();
            termSelectorHashMap.put(fieldName, termSelector);
            termSelector.name = fieldName;
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

    public void reset() {
        fieldSelector = new HashSet<>();
        termSelectorHashMap = new LinkedHashMap<>();
        rangeSelectorHashMap = new LinkedHashMap<>();
        pagingSelector = new PagingSelector();
        sortSelector = new SortSelector();
    }


    public String build() {
        try {

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            if (fieldSelector.size() > 0) {
                StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append("\"").append(FIELDS).append("\"").append(":").append(MakaanBuyerApplication.gson.toJson(fieldSelector));
                jsonBuilder.append(fieldBuilder.toString());
            }

            if (termSelectorHashMap.size() > 0 || rangeSelectorHashMap.size() > 0) {

                StringBuilder andStrBuilder = new StringBuilder();
                andStrBuilder.append("\"").append(AND).append("\"").append(":[");

                int i = 0;
                for (LinkedHashMap.Entry<String, TermSelector> entry : termSelectorHashMap.entrySet()) {

                    String termSelectorJson = entry.getValue().build();
                    if (!StringUtil.isBlank(termSelectorJson)) {

                        if (i != 0 || fieldSelector.size() > 0) {
                            andStrBuilder.append(",").append(termSelectorJson);
                        } else {
                            andStrBuilder.append(termSelectorJson);
                        }
                        i++;
                    }
                }

                int j = 0;
                for (LinkedHashMap.Entry<String, RangeSelector> entry : rangeSelectorHashMap.entrySet()) {

                    String rangeSelectorJson = entry.getValue().build();
                    if (!StringUtil.isBlank(rangeSelectorJson)) {
                        if (j != 0 || i > 0) {          // i > 0 means at least one term selector added
                            andStrBuilder.append(",").append(rangeSelectorJson);
                        } else {
                            andStrBuilder.append(rangeSelectorJson);
                        }
                        j++;
                    }
                }

                andStrBuilder.append("]");
                jsonBuilder.append("\"").append(FILTERS).append("\"").append(":{").append(andStrBuilder.toString()).append("}");
            }

            String pagingSelectorJson = pagingSelector.build();
            if (!StringUtil.isBlank(pagingSelectorJson)) {
                jsonBuilder.append(",\"").append(PAGING).append("\"").append(":").append(pagingSelectorJson);
            }

            String sortSelectorJson = sortSelector.build();
            if (!StringUtil.isBlank(sortSelectorJson)) {
                jsonBuilder.append(",\"").append(SORT).append("\"").append(":").append(sortSelectorJson);
            }

            jsonBuilder.append("}");

            return SELECTOR.concat("=").concat(jsonBuilder.toString());

        } catch (Exception e) {
            Log.e(TAG, "Error while building Selector", e);
        }


        return null;
    }


}
