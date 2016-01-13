package com.makaan.event.serp;

import android.util.Log;

import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingData;
import com.makaan.response.parser.ListingParser;
import com.makaan.util.AppBus;

import static com.makaan.constants.ResponseConstants.*;
import static com.makaan.constants.MessageConstants.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vaibhav on 24/12/15.
 * <p/>
 * parser for listing get callback
 */
public class BaseSerpCallback extends JSONGetCallback {
    public static final String TAG = BaseSerpCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {
        SerpGetEvent serpGetEvent = new SerpGetEvent();


        if (null != dataResponse) {
            try {

                JSONObject apiResponse = dataResponse.getJSONObject(ResponseConstants.DATA);
                if (null != apiResponse) {
                    ListingData listingData = new ListingData();
                    //listingData.totalCount = apiResponse.optInt(TOTAL_COUNT);
                    serpGetEvent.listingData = listingData;

                    JSONArray items = apiResponse.getJSONArray(ITEMS);
                    if (null == items || items.length() == 0) {
                        serpGetEvent.message = SERP_NO_LISITINGS_AVAILABLE;
                    } else {

                        ListingParser listingParser = new ListingParser();
                        for (int i = 0; i < items.length(); i++) {

                            JSONObject listingJson = items.getJSONObject(i);

                            listingJson = listingJson.getJSONObject(MIN_PRICE_PER_UNIT_AREA);
                            Listing listing = listingParser.getListingFromJson(listingJson);

                            listingData.listings.add(listing);
                            listingData.cityName = listing.cityName;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse lisiting data", e);
            }

        } else {
            serpGetEvent.message = SERP_NO_LISITINGS_AVAILABLE;

        }

        AppBus.getInstance().post(serpGetEvent);

    }


}
