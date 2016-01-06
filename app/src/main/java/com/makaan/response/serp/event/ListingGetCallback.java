package com.makaan.response.serp.event;

import android.util.Log;

import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.listing.LisitingPostedBy;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingData;
import com.makaan.response.listing.ListingImage;
import com.makaan.response.parser.ListingParser;
import com.makaan.response.project.Project;
import com.makaan.util.AppBus;

import static com.makaan.constants.ResponseConstants.*;
import static com.makaan.util.AppUtils.*;
import static com.makaan.util.ListingUtil.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by vaibhav on 24/12/15.
 * <p/>
 * parser for listing get callback
 */
public class ListingGetCallback extends JSONGetCallback {
    public static final String TAG = ListingGetCallback.class.getSimpleName();

    public static final String NO_LISITINGS_AVAILABLE = "No Lisitings available";


    @Override
    public void onSuccess(JSONObject apiResponse) {
        ListingGetEvent listingGetEvent = new ListingGetEvent();

        if (null != apiResponse) {
            try {
                ListingData listingData = new ListingData();
                //listingData.totalCount = apiResponse.optInt(TOTAL_COUNT);
                listingGetEvent.listingData = listingData;

                JSONArray items = apiResponse.getJSONArray(ITEMS);
                if (null == items || items.length() == 0) {
                    listingGetEvent.message = NO_LISITINGS_AVAILABLE;
                } else {

                    ListingParser listingParser = new ListingParser();
                    for (int i = 0; i < items.length(); i++) {

                        JSONObject apiListing = items.getJSONObject(i);
                        Listing listing = listingParser.getListingFromJson(apiListing);

                        listingData.listings.add(listing);
                        listingData.cityName = listing.cityName;
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse lisiting data", e);
            }

        } else {
            listingGetEvent.message = NO_LISITINGS_AVAILABLE;

        }

        AppBus.getInstance().post(listingGetEvent);

    }


}
