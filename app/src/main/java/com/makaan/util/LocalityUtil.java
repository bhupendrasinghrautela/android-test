package com.makaan.util;

import com.makaan.constants.RequestConstants;
import com.makaan.response.locality.LifeStyleImages;
import com.makaan.response.locality.ListingAggregation;

import java.util.ArrayList;
import java.util.HashMap;

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
                if(ListingAggregation.avgPricePerUnitArea!=null) {
                    saleMedian += ListingAggregation.avgPricePerUnitArea * ListingAggregation.count;
                    countsSales += ListingAggregation.count;
                }
            }
        }
        if(countsSales!=0)
            saleMedian = saleMedian / countsSales;
        return saleMedian;
    }

    public static Double calculateRentalPrice(ArrayList<ListingAggregation> listingAggregations) {
        double saleMedian = 0;
        int countsSales = 0;
        for (ListingAggregation ListingAggregation:listingAggregations) {
            if (ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.RENTAL)) {
                if(ListingAggregation.avgPrice!=null) {
                    saleMedian += ListingAggregation.avgPrice * ListingAggregation.count;
                    countsSales += ListingAggregation.count;
                }
            }
        }
        if(countsSales!=0)
            saleMedian = saleMedian / countsSales;
        return saleMedian;
    }

    public static HashMap<String,String> getImageHashMap(ArrayList<LifeStyleImages> lifeStyleImagesArrayList){
        HashMap<String,String> imagesHashMap = new HashMap<>();
        for(LifeStyleImages lifeStyleImages:lifeStyleImagesArrayList){
            if(lifeStyleImages.imageType!=null && lifeStyleImages.imageType.displayName!=null) {
                imagesHashMap.put(lifeStyleImages.imageType.displayName.toLowerCase(), lifeStyleImages.absolutePath);
            }
        }
        return imagesHashMap;
    }
}
