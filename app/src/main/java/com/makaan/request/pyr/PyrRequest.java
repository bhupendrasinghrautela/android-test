package com.makaan.request.pyr;

/**
 * Created by makaanuser on 9/1/16.
 */
public class PyrRequest {

    private String name="asg";
    private String email="asgairola@gmail.com";
    private String phone="8789437474";
    private int countryId=1;
    private String country="India";
    private double minBudget= 10000;
    private double maxBudget=100000;
    private int domainId=1;
    private String [] propertyTypes ={"apartment"};
    private String  salesType="rent";
    private int [] bhk={2,3};
    private int cityId=11;
    private boolean sendOtp=true;
    private int [] multipleSellerIds={123,123};
    private int [] localityIds={32,43};
    private PyrEnquiryType enquiryType;

    public int[] getBhk() {
        return bhk;
    }

    public void setBhk(int[] bhk) {
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

    public int[] getMultipleSellerIds() {
        return multipleSellerIds;
    }

    public void setMultipleSellerIds(int[] multipleSellerIds) {
        this.multipleSellerIds = multipleSellerIds;
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

    public String[] getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(String[] propertyTypes) {
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
}
