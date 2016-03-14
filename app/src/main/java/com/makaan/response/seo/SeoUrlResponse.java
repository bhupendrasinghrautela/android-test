package com.makaan.response.seo;

import com.makaan.response.BaseResponse;

/**
 * Created by sunil on 24/06/15.
 *
 * Model for SEO url api response
 */
public class SeoUrlResponse extends BaseResponse {

    public Data data;

    public static class Data {

        public String templateId;
        public UrlDetail urlDetail;

    }

    public static class UrlDetail {
        public long projectId;
        public long cityId;
        public long listingId;
        public long localityId;
        public String listingFilter;
        public String cityName;

    }

}
