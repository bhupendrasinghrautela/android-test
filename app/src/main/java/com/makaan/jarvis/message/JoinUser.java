package com.makaan.jarvis.message;

import com.makaan.jarvis.JarvisConstants;

import java.util.Date;

/**
 * Created by sunil on 18/01/16.
 */
public class JoinUser {
    private Object agentObject;
    private boolean isAcquired;

    public JoinUser(Object object, boolean acquired){
        agentObject = object;
        isAcquired = acquired;
    }

    //TODO dummy join user data
    public String deliveryId = JarvisConstants.DELIVERY_ID;
    public boolean acquired = isAcquired;
    public Date joinedTime = new Date(System.currentTimeMillis());
    public Object acquiredBy = agentObject;
    public String location  = "https://makaan.com/android";
    public boolean detected  = false;
    public String type = "android";
}
