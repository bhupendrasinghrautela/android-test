package com.makaan.request.pyr;

import java.util.ArrayList;

/**
 * Created by makaanuser on 9/1/16.
 */
public class PyrRequest {

    private String name;
    private String email;
    private String phone;
    private int countryId;
    private String country;
    private double minBudget;
    private double maxBudget;
    private int domainId;
    private ArrayList<String> propertyTypes;
    private String salesType;
    private ArrayList<Integer> bhk;
    private int cityId;
    private boolean sendOtp;
    private Long[] multipleCompanyIds;
    private int[] localityIds;
    private PyrEnquiryType enquiryType;
    private String applicationType;
    private String pageType;
    private Long projectId;
    private Long listingId;
//    private Long propertyId;
    private String cityName;
    private String projectName;

    /*public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }*/

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public ArrayList<Integer> getBhk() {
        return bhk;
    }

    public void setBhk(ArrayList<Integer> bhk) {
        this.bhk = bhk;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PyrEnquiryType getEnquiryType() {
        return enquiryType;
    }

    public void setEnquiryType(PyrEnquiryType enquiryType) {
        this.enquiryType = enquiryType;
    }

    public int[] getLocalityIds() {
        return localityIds;
    }

    public void setLocalityIds(int[] localityIds) {
        this.localityIds = localityIds;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public double getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(double minBudget) {
        this.minBudget = minBudget;
    }

    public Long[] getMultipleCompanyIds() {
        return multipleCompanyIds;
    }

    public void setMultipleCompanyIds(Long[] multipleCompanyIds) {
        this.multipleCompanyIds = multipleCompanyIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(ArrayList<String> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public boolean isSendOtp() {
        return sendOtp;
    }

    public void setSendOtp(boolean sendOtp) {
        this.sendOtp = sendOtp;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

}
