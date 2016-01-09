package com.makaan.pojo;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class TempClusterItem {
    public String getPropertyPriceRange() {
        return propertyPriceRange;
    }

    public void setPropertyPriceRange(String propertyPriceRange) {
        this.propertyPriceRange = propertyPriceRange;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public String getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(String propertyCount) {
        this.propertyCount = propertyCount;
    }

    String propertyPriceRange, propertyType, propertyAddress, propertyCount;

    public TempClusterItem(String propertyPriceRange, String propertyType, String propertyAddress, String propertyCount) {
        this.propertyPriceRange = propertyPriceRange;
        this.propertyType = propertyType;
        this.propertyAddress = propertyAddress;
        this.propertyCount = propertyCount;
    }
}
