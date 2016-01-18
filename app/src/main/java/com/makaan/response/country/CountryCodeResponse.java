package com.makaan.response.country;



import com.makaan.response.BaseResponse;

import java.util.List;

/**
 * Created by aishwarya on 13/7/15.
 */
public class CountryCodeResponse extends BaseResponse {
    private List<CountryCodeData> data;

    public List<CountryCodeData> getData() {
        return data;
    }

    public void setData(List<CountryCodeData> data) {
        this.data = data;
    }

    public static class CountryCodeData {
        private Integer countryId;
        private String label;
        private String countryCode;

        public Integer getCountryId() {
            return countryId;
        }

        public void setCountryId(Integer countryId) {
            this.countryId = countryId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }
}
