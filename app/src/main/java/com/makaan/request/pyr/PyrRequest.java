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
    private Long[] multipleSellerIds;
    private int[] localityIds;
    private PyrEnquiryType enquiryType;

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

    public Long[] getMultipleSellerIds() {
        return multipleSellerIds;
    }

    public void setMultipleSellerIds(Long[] multipleSellerIds) {
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
}
