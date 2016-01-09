package com.makaan.event.listing;

import android.util.Log;

import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.Listing;
import com.makaan.response.parser.ListingParser;
import com.makaan.util.AppBus;

import org.json.JSONObject;

import static com.makaan.constants.MessageConstants.*;

/**
 * Created by vaibhav on 07/01/16.
 *
 */
public class ListingByIdCallback extends JSONGetCallback {

    public static final String TAG = MakaanNetworkClient.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseData) {

        ListingByIdGetEvent listingByIdGetEvent = new ListingByIdGetEvent();

        try {
            JSONObject apiListing = responseData.getJSONObject(ResponseConstants.DATA);

            if (null != apiListing) {
                ListingParser listingParser = new ListingParser();
                listingByIdGetEvent.listing = listingParser.getListingFromJson(apiListing);
            } else {
                listingByIdGetEvent.message = LISTING_DETAILS_NOT_AVAIL;
            }


            AppBus.getInstance().post(listingByIdGetEvent);
        }catch(Exception e){
            Log.e(TAG, "Error parsing serp listing",e);
        }
    }
}
