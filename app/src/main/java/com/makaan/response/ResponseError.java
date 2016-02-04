package com.makaan.response;

import com.android.volley.VolleyError;

/**
 * Created by sunil on 17/12/15.
 */
public class ResponseError {
    private VolleyError error;
    private String msg;
    private String statusCode;

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError volleyError) {
        this.error = volleyError;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
