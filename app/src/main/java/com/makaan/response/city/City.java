package com.makaan.response.city;

import com.makaan.response.locality.ListingAggregation;

import java.util.ArrayList;

/**
 * Created by vaibhav on 09/01/16.
 */
public class City {

    public Long id;
    public Double centerLatitude,centerLongitude, annualGrowth, rentalYield, demandRate, supplyRate,localityMaxSafetyScore,
            localityMinSafetyScore,localityMaxLivabilityScore, localityMinLivabilityScore,minAffordablePrice,maxBudgetPrice, maxAffordablePrice, minLuxuryPrice;
    public String description;
    public String label;
    public String cityTagLine;

    public String cityHeroshotImageUrl;
    public Double avgPricePerUnitArea, averageRentPerMonth;


    public ArrayList<ListingAggregation> listingAggregations = new ArrayList<>();
    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();
}
