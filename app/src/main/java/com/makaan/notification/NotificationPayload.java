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

    //ActivityLauncherNotification :
    //Activity to launched as pending intent; corresponding to ActivityType
    private int screenTypeId;

    //image url for big image expandable
    private String imageUrl;

    //target url for browser notification
    private String targetUrl;

    //Sorting order
    private int sortOrder;

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

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
