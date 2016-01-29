package com.makaan.response.leadForm;

/**
 * Created by makaanuser on 25/1/16.
 */
public class InstantCallbackResponse {

    private String statusCode;
    private String version;
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
