package com.makaan.event.listing;

import com.makaan.network.JSONGetCallback;
import com.makaan.response.listing.Listing;
import com.makaan.response.parser.ListingParser;
import com.makaan.util.AppBus;

import org.json.JSONObject;

import static com.makaan.constants.MessageConstants.*;

/**
 * Created by vaibhav on 07/01/16.
 */
public class ListingByIdCallback extends JSONGetCallback {


    @Override
    public void onSuccess(JSONObject apiListing) {

        ListingByIdGetEvent listingByIdGetEvent = new ListingByIdGetEvent();

        if (null != apiListing) {
            ListingParser listingParser = new ListingParser();
            listingByIdGetEvent.listing = listingParser.getListingFromJson(apiListing);
        } else {
            listingByIdGetEvent.message = LISTING_DETAILS_NOT_AVAIL;
        }


        AppBus.getInstance().post(listingByIdGetEvent);
    }
}
