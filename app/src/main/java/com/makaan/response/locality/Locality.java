package com.makaan.response.locality;

import com.makaan.response.city.EntityDesc;

import java.util.ArrayList;

/**
 * Created by vaibhav on 09/01/16.
 */
public class Locality {

    public String label, description;
    public Long cityId, localityId;
    public Double livabilityScore, latitude, longitude,
            avgPriceRisePercentage, avgPricePerUnitArea, averageRentPerMonth, minAffordablePrice,
            maxBudgetPrice, maxAffordablePrice, minLuxuryPrice;
    public Integer avgRentalDemandRisePercentage;
    public String localityHeroshotImageUrl, cityHeroshotImageUrl;
    public Suburb suburb;

    public ArrayList<PropertyDerivedInfo> propertyDerivedInfos = new ArrayList<>();

    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();

}
