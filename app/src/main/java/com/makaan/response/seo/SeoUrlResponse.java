package com.makaan.response.seo;

import com.makaan.response.BaseResponse;

/**
 * Created by sunil on 24/06/15.
 *
 * Model for SEO url api response
 */
public class SeoUrlResponse extends BaseResponse {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        private String templateId;
        private UrlDetail urlDetail;


        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public UrlDetail getUrlDetail() {
            return urlDetail;
        }

        public void setUrlDetail(UrlDetail urlDetail) {
            this.urlDetail = urlDetail;
        }
    }

    public static class UrlDetail {
        private long projectId;
        private String listingFilter;
        private String cityName;

        public long getProjectId() {
            return projectId;
        }

        public void setProjectId(long projectId) {
            this.projectId = projectId;
        }

        public String getListingFilter() {
            return listingFilter;
        }

        public void setListingFilter(String listingFilter) {
            this.listingFilter = listingFilter;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }
    }

}
