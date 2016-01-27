package com.makaan.response.city;

/**
 * Created by vaibhav on 22/01/16.
 */
public class CityTrendData {

    public Double minPrice;
    public Double maxPrice;
    public int noOfListings;

    public CityTrendData(Double minPrice, Double maxPrice, int noOfListings) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.noOfListings = noOfListings;
    }

    public CityTrendData() {
    }
}
