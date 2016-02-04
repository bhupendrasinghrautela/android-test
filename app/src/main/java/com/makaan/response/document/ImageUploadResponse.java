package com.makaan.response.document;

import com.makaan.response.ResponseError;

/**
 * Created by rohitgarg on 2/4/16.
 */
public class ImageUploadResponse {
    private ImageUploadResponseData data;
    private String statusCode;
    private String version;

    public ImageUploadResponseData getData() {
        return data;
    }

    public void setData(ImageUploadResponseData data) {
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

    private ResponseError error;


    public ResponseError getError() {
        return error;
    }

    public void setError(ResponseError error) {
        this.error = error;
    }

    public class ImageUploadResponseData {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}