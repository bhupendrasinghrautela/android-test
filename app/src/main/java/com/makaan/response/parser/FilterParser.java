/*
package com.makaan.response.parser;

import android.util.Log;

import com.makaan.response.filter.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.makaan.constants.ResponseConstants.FILTER_DISPLAY_NAME;
import static com.makaan.constants.ResponseConstants.FILTER_FIELD_NAME;
import static com.makaan.constants.ResponseConstants.FILTER_DISPLAY_ORDER;
import static com.makaan.constants.ResponseConstants.FILTER_INTERNAL_NAME;
import static com.makaan.constants.ResponseConstants.TERM_FILTER_VALUES;
import static com.makaan.constants.ResponseConstants.RANGE_FILTER_VALUES;
import static com.makaan.constants.ResponseConstants.FILTER_TERM_VALUE;
import static com.makaan.constants.ResponseConstants.FILTER_RANGE_MIN_VALUE;
import static com.makaan.constants.ResponseConstants.FILTER_RANGE_MAX_VALUE;
import static com.makaan.constants.ResponseConstants.FILTER_SELECTED;

*/
/**
 * Created by rohitgarg on 1/8/16.
 *//*

public class FilterParser {

    public static final String TAG = FilterParser.class.getSimpleName();

    public Filter getFiltersFromJson(JSONObject filterJson) {
        Filter filter = new Filter();

        try {
            if (null != filterJson) {
                filter.displayName = filterJson.optString(FILTER_DISPLAY_NAME);
                filter.internalName = filterJson.optString(FILTER_INTERNAL_NAME);
                filter.displayOrder = filterJson.optInt(FILTER_DISPLAY_ORDER);

                JSONArray termFilterValue = filterJson.getJSONArray(TERM_FILTER_VALUES);
                JSONArray rangeFilterValue = filterJson.getJSONArray(RANGE_FILTER_VALUES);

                if(termFilterValue != null && termFilterValue.length() > 0) {
                    filter.termFilters = new ArrayList<Filter.TermFilter>();

                    for(int i = 0; i < termFilterValue.length(); i++) {
                        Filter.TermFilter termFilter = filter.new TermFilter();
                        JSONObject obj = termFilterValue.getJSONObject(i);
                        termFilter.displayName = obj.optString(FILTER_DISPLAY_NAME);
                        termFilter.fieldName = obj.optString(FILTER_FIELD_NAME);
                        termFilter.displayOrder = obj.optInt(FILTER_DISPLAY_ORDER);
                        termFilter.value = obj.optInt(FILTER_TERM_VALUE);
                        termFilter.selected = obj.optBoolean(FILTER_SELECTED);
                        filter.termFilters.add(termFilter);
                    }
                }
                if(rangeFilterValue != null && rangeFilterValue.length() > 0) {
                    filter.rangeFiler = filter.new RangeFilter();
                    filter.rangeFiler.displayName = rangeFilterValue.getJSONObject(0).optString(FILTER_DISPLAY_NAME);
                    filter.rangeFiler.fieldName = rangeFilterValue.getJSONObject(0).optString(FILTER_FIELD_NAME);
                    filter.rangeFiler.displayOrder = rangeFilterValue.getJSONObject(0).optInt(FILTER_DISPLAY_ORDER);
                    filter.rangeFiler.minValue = rangeFilterValue.getJSONObject(0).optInt(FILTER_RANGE_MIN_VALUE);
                    filter.rangeFiler.minValue = rangeFilterValue.getJSONObject(0).optInt(FILTER_RANGE_MAX_VALUE);
                    filter.rangeFiler.selected = rangeFilterValue.getJSONObject(0).optBoolean(FILTER_SELECTED);
                }
            }
        }  catch (JSONException e) {
                Log.e(TAG, "Unable to parse lisiting data", e);
        }
        return filter;
    }
}
*/
