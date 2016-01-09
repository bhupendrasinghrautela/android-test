package com.makaan.event.filter;

import android.util.Log;

import com.makaan.network.JSONGetCallback;
import com.makaan.response.filter.Filter;
import com.makaan.response.filter.FilterData;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingData;
import com.makaan.response.parser.FilterParser;
import com.makaan.response.parser.ListingParser;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.makaan.constants.MessageConstants.FILTER_DETAILS_NOT_AVAILABLE;
import static com.makaan.constants.ResponseConstants.FILTERS;
import static com.makaan.constants.ResponseConstants.TERM_FILTER_VALUES;
import static com.makaan.constants.ResponseConstants.RANGE_FILTER_VALUES;

/**
 * Created by rohitgarg on 1/8/16.
 */
public class FilterGetCallback extends JSONGetCallback {
    public static final String TAG = FilterGetCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {
        FilterGetEvent filterGetEvent = new FilterGetEvent();
        if (null != responseObject) {
            try {
                FilterData filterData = new FilterData();
                filterGetEvent.filterData = filterData;

                JSONArray items = responseObject.getJSONArray(FILTERS);
                if (null == items || items.length() == 0) {
                    filterGetEvent.message = FILTER_DETAILS_NOT_AVAILABLE;
                } else {
                    FilterParser filterParser = new FilterParser();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject filterValuesJson = items.getJSONObject(i);

                        Filter filter = filterParser.getFiltersFromJson(filterValuesJson);

                        filterData.filters.add(filter);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse filter data", e);
            }

        } else {
            filterGetEvent.message = FILTER_DETAILS_NOT_AVAILABLE;

        }

        AppBus.getInstance().post(filterGetEvent);
    }
}
