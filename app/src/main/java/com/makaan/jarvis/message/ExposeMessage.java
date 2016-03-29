package com.makaan.jarvis.message;

import com.makaan.jarvis.event.JarvisTrackExtraData;

/**
 * Created by sunil on 23/01/16.
 */
public class ExposeMessage {

    public String sessionId;
    public Properties properties;
    public String city;


    public static class Properties{
        public String suggest_filter;
        public String type;
        public String message_type;
        public Object content;
        public CtaType ctaType;
        public JarvisTrackExtraData extra;
        public Long leadId;
        public Long agentId;
    }

}
