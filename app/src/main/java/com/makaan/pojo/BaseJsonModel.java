package com.makaan.pojo;

import com.makaan.response.ResponseError;

/**
 * Created by sunil on 03/12/15.
 */
public class BaseJsonModel {

    private String statusCode;
    private String version;

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

    private ResponseError error;


    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }


}
