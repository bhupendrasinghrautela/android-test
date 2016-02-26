package com.makaan.util;

import com.makaan.constants.RequestConstants;
import com.makaan.response.locality.ListingAggregation;

import java.util.ArrayList;

/**
 * Created by aishwarya on 26/02/16.
 */
public class LocalityUtil {

    public static Double calculateMedian(ArrayList<ListingAggregation> listingAggregations) {
        double saleMedian = 0;
        int countsSales = 0;
        for (ListingAggregation ListingAggregation:listingAggregations) {
            if (ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.PRIMARY) ||
                    ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.RESALE)) {
                saleMedian = saleMedian + ListingAggregation.avgPricePerUnitArea;
                countsSales++;
            }
        }
        if(countsSales!=0)
            saleMedian = saleMedian / countsSales;
        return saleMedian;
    }

    public static Double calculateAveragePrice(ArrayList<ListingAggregation> listingAggregations) {
        double saleMedian = 0;
        int countsSales = 0;
        for (ListingAggregation ListingAggregation:listingAggregations) {
            if (ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.PRIMARY) ||
                    ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.RESALE)) {
                saleMedian += ListingAggregation.avgPricePerUnitArea*ListingAggregation.count;
                countsSales += ListingAggregation.count;
            }
        }
        if(countsSales!=0)
            saleMedian = saleMedian / countsSales;
        return saleMedian;
    }
}
