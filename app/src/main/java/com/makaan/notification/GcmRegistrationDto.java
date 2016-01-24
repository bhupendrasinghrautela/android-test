package com.makaan.notification;

/**
 * Created by sunil on 07/12/15.
 *
 * DTO model for Gcm id registration
 */
public class GcmRegistrationDto {

    private String userKey;
    private String gcmRegId;
    private String email;
    private String appIdentifier;


    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }
    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAppIdentifier() {
        return appIdentifier;
    }
    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }



}
