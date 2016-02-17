package com.makaan.analytics;

import android.content.Context;

import java.util.Map;

/**
 * Created by sunil on 17/02/16.
 */
public class MakaanEventTracker {

    private static MakaanEventTracker instance;

    private MakaanEventTracker(){
    }

    public static synchronized MakaanEventTracker getInstance(){
        if(null==instance){
            instance = new MakaanEventTracker();
        }

        return instance;
    }


    //TODO temp for testing
    /*Analytics.with(getActivity()).track(JarvisConstants.DELIVERY_ID, new Properties()
    .putValue("listing_id", mListings.get(9).id)
    .putValue("serp_visible_item", 9)
    .putValue("serp_filter_budget", null)
    .putValue("serp_filter_property_type", null)

    .putValue("serp_filter_bhk", null));*/

    public static void track(Context context, MakaanTrackerConstants.Category category, MakaanTrackerConstants.Action action){

    }

}
