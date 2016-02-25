package com.makaan.jarvis.event;

/**
 * Created by sunil on 24/02/16.
 */
public class JarvisTrackExtraData {
    public String page_type;
    public long pageTimestamp;

    public void setPageType(String pageType){
        this.page_type = pageType;
    }

    public void setPageTimeStamp(long timeStamp){
        this.pageTimestamp = timeStamp;
    }
}
