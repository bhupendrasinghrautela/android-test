package com.makaan.response.locality;

import com.makaan.constants.StringConstants;
import com.makaan.response.city.EntityDesc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vaibhav on 09/01/16.
 */

/**
 * overview section : median price, median rent, avgPriceRisePercentage,averageRentPerMonth
 */
public class Locality {

    public String label, description,constructionStatus;
    public Long cityId, localityId;
    public Double livabilityScore, latitude, longitude,localityLivabilityRank,
            avgPriceRisePercentage, averageRentPerMonth, minAffordablePrice,maxBudgetPrice, maxAffordablePrice, minLuxuryPrice,avgPricePerUnitAreaVilla,
            priceRiseRankPercentage, avgPriceRisePercentageApartment, avgPriceRiseMonths,avgPricePerUnitArea,avgPricePerUnitAreaPlot;
    public Double avgRentalDemandRisePercentage,priceRise6Months;
    public String localityHeroshotImageUrl, cityHeroshotImageUrl,dominantUnitType;
    public Suburb suburb;
    public int constructionStatusId;
    public Integer projectCount;


    public HashMap<String, Integer> amenityTypeCount;


    public ArrayList<ListingAggregation> listingAggregations = new ArrayList<>();
    public ArrayList<LifeStyleImages> images;
    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();


    public String getDescription() {
//        return AppUtils.stripHtml(description);
        return description;
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
