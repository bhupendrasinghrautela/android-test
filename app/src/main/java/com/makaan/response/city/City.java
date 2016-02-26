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
    public Double cityBuyMinPrice,cityBuyMaxPrice,cityRentMinPrice,cityRentMaxPrice;


    public ArrayList<ListingAggregation> listingAggregations = new ArrayList<>();
    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", centerLatitude=" + centerLatitude +
                ", centerLongitude=" + centerLongitude +
                ", annualGrowth=" + annualGrowth +
                ", rentalYield=" + rentalYield +
                ", demandRate=" + demandRate +
                ", supplyRate=" + supplyRate +
                ", label='" + label + '\'' +
                ", cityTagLine='" + cityTagLine + '\'' +
                ", cityHeroshotImageUrl='" + cityHeroshotImageUrl + '\'' +
                ", avgPricePerUnitArea=" + avgPricePerUnitArea +
                ", averageRentPerMonth=" + averageRentPerMonth +
                ", listingAggregations=" + listingAggregations +
                ", entityDescriptions=" + entityDescriptions +
                '}';
    }
}
