package com.makaan.jarvis.message;

import com.makaan.jarvis.JarvisConstants;

import java.util.Date;

/**
 * Created by sunil on 18/01/16.
 */
public class JoinUser {
    //TODO dummy join user data
    public String deliveryId = JarvisConstants.DELIVERY_ID;
    public boolean acquired = false;
    public Date joinedTime = new Date(System.currentTimeMillis());
    public String acquiredBy = null;
    public String location  = "https://makaan.com/android/test/motox";
    public boolean detected  = false;
    public String type = "android";
}
