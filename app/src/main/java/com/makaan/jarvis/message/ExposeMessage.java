package com.makaan.jarvis.message;

/**
 * Created by sunil on 23/01/16.
 */
public class ExposeMessage {

    public String sessionId;
    public Properties properties;


    public static class Properties{
        public String suggest_filter;
        public String type;
        public CtaType ctaType;
    }
}
