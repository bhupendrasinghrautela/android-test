package com.makaan.jarvis.analytics;

import com.makaan.activity.listing.SerpActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ApiConstants;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.jarvis.event.JarvisTrackExtraData;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.service.MakaanService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.JsonBuilder;
import com.segment.analytics.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by sunil on 11/02/16.
 */
public class AnalyticsService implements MakaanService {

    private static final String TAG = AnalyticsService.class.getSimpleName();

    public enum Type{
        identify, track;
    }

    public void trackSerpScroll(Set<String> selectedFiltersName, int position, JarvisTrackExtraData jarvisTrackExtraData){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AnalyticsConstants.KEY_SERP_VISIBLE_ITEM, position);
            jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.SERP_SCROLL);
            jsonObject.put(AnalyticsConstants.KEY_EXTRA, JsonBuilder.toJson(jarvisTrackExtraData));

            Map<String, SerpFilterMessageMap> filterMessageMapMap = MasterDataCache.getInstance().getSerpFilterMessageMap();
            Iterator iterator = filterMessageMapMap.entrySet().iterator();

            if(null!=selectedFiltersName) {
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    SerpFilterMessageMap serpFilterMessageMap = (SerpFilterMessageMap) pair.getValue();

                    if (selectedFiltersName.contains(serpFilterMessageMap.internalName)) {
                        jsonObject.put(serpFilterMessageMap.filter, true);
                    } else {
                        jsonObject.put(serpFilterMessageMap.filter, null);
                    }
                }
            }

            track(AnalyticsService.Type.track, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trackBuyerJourney(String eventName, JarvisTrackExtraData jarvisTrackExtraData){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, eventName);
            jsonObject.put(AnalyticsConstants.KEY_EXTRA, JsonBuilder.toJson(jarvisTrackExtraData));

            track(AnalyticsService.Type.track, jsonObject);
        }catch (Exception e){}
    }

    public synchronized void track(Type type, JSONObject object){
        if(object==null){
            return;
        }

        try {
            JSONObject data = new JSONObject();
            data.put(AnalyticsConstants.KEY_VISITOR_ID, JarvisConstants.DELIVERY_ID);
            if (type == Type.identify) {
                data.put(AnalyticsConstants.KEY_TRACK_TYPE,AnalyticsConstants.IDENTIFY);
            }

            if (type == Type.track) {
                data.put(AnalyticsConstants.KEY_TRACK_TYPE,AnalyticsConstants.TRACK);
            }

            object.put(AnalyticsConstants.KEY_DELIVERY_ID, JarvisConstants.DELIVERY_ID);

            data.put(AnalyticsConstants.KEY_TRAITS, object);

            MakaanNetworkClient.getInstance().postTrack(getUrl(), data, null, TAG);
        }catch (Exception e){}
    }


    private String getUrl(){
        return ApiConstants.BASE_URL+"/apis/mpAnalytics";
    }

}
