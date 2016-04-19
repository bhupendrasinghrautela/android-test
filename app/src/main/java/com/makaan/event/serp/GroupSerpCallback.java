package com.makaan.event.serp;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.GroupListingData;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.MessageConstants.SERP_NO_LISITINGS_AVAILABLE;
import static com.makaan.constants.ResponseConstants.ITEMS;
import static com.makaan.constants.ResponseConstants.TOTAL_COUNT;

/**
 * Created by vaibhav on 24/12/15.
 * <p/>
 * parser for listing get callback
 */
public class GroupSerpCallback extends JSONGetCallback {
    public static final String TAG = GroupSerpCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {
        GroupSerpGetEvent groupSerpGetEvent = new GroupSerpGetEvent();
        final Type groupTypeList = new TypeToken<ArrayList<GroupListing>>() {}.getType();


        if (null != dataResponse) {
            try {
                JSONObject apiResponse = dataResponse.getJSONObject(ResponseConstants.DATA);
                if (null != apiResponse) {
                    GroupListingData listingData = new GroupListingData();
                    listingData.totalCount = dataResponse.optInt(TOTAL_COUNT);
                    JSONArray items = apiResponse.getJSONArray(ITEMS);
                    ArrayList<GroupListing> listings = MakaanBuyerApplication.gson.fromJson(items.toString(), groupTypeList);
                    if(listings != null) {
                        listingData.groupListings = listings;
                    }
                    groupSerpGetEvent.groupListingData = listingData;
                }
            } catch (JSONException e) {
                Crashlytics.logException(e);
                CommonUtil.TLog(TAG, "Unable to parse group listing data", e);
            }

        } else {
            groupSerpGetEvent.message = SERP_NO_LISITINGS_AVAILABLE;

        }

        AppBus.getInstance().post(groupSerpGetEvent);

    }


    @Override
    public void onError(ResponseError error) {
        GroupSerpGetEvent groupSerpGetEvent = new GroupSerpGetEvent();
        groupSerpGetEvent.error = error;
        AppBus.getInstance().post(groupSerpGetEvent);
    }
}
