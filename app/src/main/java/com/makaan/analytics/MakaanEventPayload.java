package com.makaan.analytics;

import android.content.Context;

import com.segment.analytics.Properties;

/**
 * Created by sunil on 17/02/16.
 */
public class MakaanEventPayload {
    public static final String SCREEN_NAME = "Screen name";
    public static final String CATEGORY = "Category";
    public static final String KEYWORD = "Keywords";
    public static final String LABEL="Label";
    public static final String VALUE="Value";
    public static final String LISTING_POSITION = "Listing position";
    public static final String LISTING_ID="ListingId";
    public static final String PROJECT_ID="ProjectId";
    public static final String CHARACTERS_LENGTH="characters";
    public static final String SUGGESTION_POSITION="Suggestion Position";
    public static final String SUGGESTION_TYPE="Suggestion Type";
    public static final String SUGGESTION_NAME="Suggestion name";
    public static final String SUGGESTION_STRING="Label";
    public static final String PRICE="Price";
    public static final String SELLER_RATING="Seller Rating";
    public static final String DATE_POSTED="Date Posted";
    public static final String MAP="Map";
    public static final String LIST_VIEW="List View";
    public static final String CALL_NOW="Call Now Clicked";
    public static final String FAVOURITE_CLICKED="Favourite Clicked";
    public static final String OPEN_PROJECT_PAGE="Open Project Page";
    public static final String MIN_BUDGET="Min Value";
    public static final String MAX_BUDGET="Max Value";
    public static final String BHK_STRING="Bhk";
    public static final String BATHROOM="Bathroom";
    public static final String BEDROOM="Bedroom";
    public static final String MIN_AREA="Min Area";
    public static final String MAX_AREA="Max Area";
    public static final String NEW_RESALE="New Resale";
    public static final String UNDER_CONSTRUCTION="Under Construction";
    public static final String READY_TO_MOVE="Ready to move";
    public static final String MPLUS="Mplus";
    public static final String PROPERTY_TYPE="Property Type";
    public static final String LISTED_BY="Listed By";
    public static final String FILTER_STRING="Filter String";
    private static MakaanEventPayload mMakaanEventPayload;
    private static Properties mProperties;

    public static Properties beginBatch() {
        if(mMakaanEventPayload == null || mProperties == null) {
            mMakaanEventPayload = new MakaanEventPayload();
            mProperties = new Properties();
        }
        return mProperties;
    }

    public static void endBatch(Context context, MakaanTrackerConstants.Action action) {

        if(mMakaanEventPayload == null || mProperties == null) {
            // TODO
        } else {
            if(action != null) {
                MakaanEventTracker.track(context, action, mProperties);
                mProperties = null;
                mMakaanEventPayload = null;
            }
        }
    }

}
