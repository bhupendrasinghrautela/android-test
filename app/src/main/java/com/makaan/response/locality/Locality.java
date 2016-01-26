package com.makaan.response.locality;

import com.makaan.constants.StringConstants;
import com.makaan.response.city.EntityDesc;
import com.makaan.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by vaibhav on 09/01/16.
 */

/**
 * overview section : median price, median rent, avgPriceRisePercentage,averageRentPerMonth
 */
public class Locality {

    public String label, description;
    public Long cityId, localityId;
    public Double livabilityScore, latitude, longitude,
            avgPriceRisePercentage, avgPricePerUnitArea, averageRentPerMonth, minAffordablePrice,
            maxBudgetPrice, maxAffordablePrice, minLuxuryPrice, priceRiseRankPercentage;
    public Double avgRentalDemandRisePercentage;
    public String localityHeroshotImageUrl, cityHeroshotImageUrl;
    public Suburb suburb;

    public ArrayList<ListingAggregation> listingAggregations = new ArrayList<>();

    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();


    public String getDescription() {
        return AppUtils.stripHtml(description);
    }

    public Double getCityMedianPrice() {
        if (null != suburb && null != suburb.city && null != suburb.city.avgPricePerUnitArea) {
            return suburb.city.avgPricePerUnitArea;
        }
        return null;
    }


    public Double getCityMedianRent() {
        if (null != suburb && null != suburb.city && null != suburb.city.averageRentPerMonth) {
            return suburb.city.averageRentPerMonth;
        }
        return null;
    }

    public String getAverageRentalDemanRise() {
        return (null != this.avgRentalDemandRisePercentage && this.avgRentalDemandRisePercentage >= 0) ? StringConstants.AVERAGE : StringConstants.AVERAGE;
    }

    @Override
    public String toString() {
        return "Locality{" +
                "label='" + label + '\'' +
                ", cityId=" + cityId +
                ", localityId=" + localityId +
                ", livabilityScore=" + livabilityScore +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", avgPriceRisePercentage=" + avgPriceRisePercentage +
                ", avgPricePerUnitArea=" + avgPricePerUnitArea +
                ", averageRentPerMonth=" + averageRentPerMonth +
                ", minAffordablePrice=" + minAffordablePrice +
                ", maxBudgetPrice=" + maxBudgetPrice +
                ", maxAffordablePrice=" + maxAffordablePrice +
                ", minLuxuryPrice=" + minLuxuryPrice +
                ", priceRiseRankPercentage=" + priceRiseRankPercentage +
                ", avgRentalDemandRisePercentage=" + avgRentalDemandRisePercentage +
                ", localityHeroshotImageUrl='" + localityHeroshotImageUrl + '\'' +
                ", cityHeroshotImageUrl='" + cityHeroshotImageUrl + '\'' +
                ", suburb=" + suburb +
                ", listingAggregations=" + listingAggregations +
                ", entityDescriptions=" + entityDescriptions +
                '}';
    }
}
