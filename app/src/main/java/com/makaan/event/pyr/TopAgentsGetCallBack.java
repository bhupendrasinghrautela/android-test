package com.makaan.event.pyr;

import android.util.Log;

import com.google.gson.Gson;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingData;
import com.makaan.response.parser.ListingParser;
import com.makaan.response.pyr.TopAgentsData;
import com.makaan.response.pyr.TopAgentsResponse;
import com.makaan.util.AppBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.makaan.constants.MessageConstants.SERP_NO_LISITINGS_AVAILABLE;
import static com.makaan.constants.MessageConstants.TOP_AGENTS_NOT_AVAILABLE;
import static com.makaan.constants.ResponseConstants.DATA;
import static com.makaan.constants.ResponseConstants.ITEMS;
import static com.makaan.constants.ResponseConstants.LISTING;

/**
 * Created by makaanuser on 9/1/16.
 */
public class TopAgentsGetCallBack extends JSONGetCallback {
    public static final String TAG = TopAgentsGetCallBack.class.getSimpleName();
    @Override
    public void onSuccess(JSONObject apiResponse) {
        Log.e("response sellers",apiResponse.toString());
        TopAgentsGetEvent topAgentsGetEvent = new TopAgentsGetEvent();

        if (null != apiResponse) {
            TopAgentsResponse topAgentsResponse= new Gson().fromJson(String.valueOf(apiResponse),TopAgentsResponse.class);
            topAgentsGetEvent.setTopAgentsResponse(topAgentsResponse);
        }
        else
        {
            topAgentsGetEvent.message = TOP_AGENTS_NOT_AVAILABLE;
        }

        AppBus.getInstance().post(topAgentsGetEvent);

    }
}
