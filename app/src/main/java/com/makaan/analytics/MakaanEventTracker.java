package com.makaan.analytics;

import android.content.Context;

import com.makaan.analytics.MakaanTrackerConstants.Action;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import java.util.Map;

/**
 * Created by sunil on 17/02/16.
 */
public class MakaanEventTracker {

    public static void track(Context context, Action action, Properties makaanEventPayload){

        switch (action){
            case searchPropertyBuy:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchHomeBuy:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchSerpBuy:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchProjectBuy:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchPropertyRent:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchProjectRent:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchHomeRent:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case searchSerpRent:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.CATEGORY, makaanEventPayload.get(MakaanEventPayload.CATEGORY));
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.get(MakaanEventPayload.LABEL));
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            case selectFilterMore:{
                Properties properties=new Properties();
                properties.put(MakaanEventPayload.LABEL, makaanEventPayload.toString());
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.searchFilter);
                Analytics.with(context).track(action.getValue() ,properties);
                Analytics.with(context).flush();
                break;
            }
            default:{
                Analytics.with(context).track(action.getValue() ,makaanEventPayload);
                Analytics.with(context).flush();
            }
        }
    }

}
