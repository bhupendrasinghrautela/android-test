package com.makaan.pojo;

/**
 * Created by aishwarya on 22/03/16.
 */
public class VersionUpdate {
    private Integer mandatoryVersionCode;
    private Integer currentVersionCode;
    private String message;

    public Integer getMandatoryVersionCode() {
        return mandatoryVersionCode;
    }

    public void setMandatoryVersionCode(Integer mandatoryVersionCode) {
        this.mandatoryVersionCode = mandatoryVersionCode;
    }

    public Integer getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(Integer currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
