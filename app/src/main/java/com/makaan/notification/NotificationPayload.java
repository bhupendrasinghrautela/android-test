package com.makaan.notification;

/**
 * Created by sunil on 07/12/15.
 */
public class NotificationPayload {

    private static final int DEFAULT_PRIORITY = 30;

    //notificationId
    private int notificationId;

    //Campaign name for tracking
    private String campaignName;

    // Default priority 30 in case not available
    private int priority = DEFAULT_PRIORITY;

    private int notificationTypeId;

    //ScreenLauncherNotification :
    //Activity to launched as pending intent; corresponding to ActivityType
    private int screenTypeId;

    //image url for big image expandable
    private String imageUrl;

    //target url for browser notification
    private String targetUrl;

    private long cityId;

    private long localityId;

    private long projectId;

    private long listingId;

    private String serpFilterUrl;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public int getScreenTypeId() {
        return screenTypeId;
    }

    public void setScreenTypeId(int screenTypeId) {
        this.screenTypeId = screenTypeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(int notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getLocalityId() {
        return localityId;
    }

    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getListingId() {
        return listingId;
    }

    public void setListingId(long listingId) {
        this.listingId = listingId;
    }

    public String getSerpFilterUrl() {
        return serpFilterUrl;
    }

    public void setSerpFilterUrl(String serpFilterUrl) {
        this.serpFilterUrl = serpFilterUrl;
    }
}
