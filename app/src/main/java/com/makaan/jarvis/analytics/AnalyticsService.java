package com.makaan.jarvis.analytics;

import com.makaan.constants.ApiConstants;
import com.makaan.jarvis.JarvisConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.service.MakaanService;

import org.json.JSONObject;

/**
 * Created by sunil on 11/02/16.
 */
public class AnalyticsService implements MakaanService {

    private static final String TAG = AnalyticsService.class.getSimpleName();

    public enum Type{
        identify, track;
    }

    public synchronized void trackSerpScroll(){

    }

    public synchronized void track(Type type, JSONObject object){
        if(object==null){
            return;
        }

        try {
            JSONObject data = new JSONObject();
            data.put("visitor_id", JarvisConstants.DELIVERY_ID);
            if (type == Type.identify) {
                data.put("type","identify");
            }

            if (type == Type.track) {
                data.put("type","track");
            }

            data.put("traits", object);

            MakaanNetworkClient.getInstance().postTrack(getUrl(), data, null, TAG);
        }catch (Exception e){}
    }


    private String getUrl(){
        return ApiConstants.BASE_URL.concat("/apis/mpAnalytics");
    }

}
