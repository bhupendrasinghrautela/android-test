package com.makaan.response.listing.detail;

import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.response.property.Property;
import com.makaan.response.user.CompanySeller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ListingDetail {


    public Long id;
    public double latitude, longitude;
    public CurrentListingPrice currentListingPrice;
    public String description, listingCategory;
    public String expiryAt, possessionDate;
    public int facingId;
    public boolean status;
    public int noOfCarParks;
    public int propertyId, projectId;
    public String unitType, unitName, measure;
    public Integer bedrooms, bathrooms, balcony, storeRoom, halls, studyRoom, servantRoom, poojaRoom;


    public Double size, budget, minResaleOrPrimaryPrice, maxResaleOrPrimaryPrice;
    public Double avgPriceRisePercentage, bookingAmount, mainEntryRoadWidth, pricePerUnitArea;
    public int bookingStatusId, ownershipTypeId;
    public String furnished;
    public int sellerId, totalFloors, floor;
    public ArrayList<Furnishing> furnishings = new ArrayList<>();

    public ArrayList<Long> neighbourhoodAmenitiesIds = new ArrayList<>();


    public CompanySeller companySeller;
    public double securityDeposit;
    public Property property;


    public ArrayList<HashMap<String, String>> viewDirections;

    public HashMap<String, Object> specifications;


    public HashMap<String, ArrayList<SpecificaitonsUI>> getFormattedSpecifications() {
        HashMap<String, ArrayList<SpecificaitonsUI>> result = new HashMap<>();

        for (Map.Entry<String, Object> specificationEntry : specifications.entrySet()) {
            String key = specificationEntry.getKey();
            Object value = specificationEntry.getValue();

            ArrayList<SpecificaitonsUI> specificaitons = new ArrayList<>();
            if (value instanceof Map) {
                Map<String, String> valueMap = (Map<String, String>) value;

                for (Map.Entry<String, String> valueEntry : valueMap.entrySet()) {
                    SpecificaitonsUI specificaitonsUI = new SpecificaitonsUI();
                    specificaitonsUI.label1 = valueEntry.getValue();
                    specificaitonsUI.label2 = valueEntry.getKey();
                    specificaitons.add(specificaitonsUI);
                }
            } else if (value instanceof String) {

                SpecificaitonsUI specificaitonsUI = new SpecificaitonsUI();
                specificaitonsUI.label1 = (String) value;
                specificaitons.add(specificaitonsUI);
            }
            result.put(key, specificaitons);
        }

        return result;
    }

    public String getDescription() {
        //return AppUtils.stripHtml(description);
        return description;
    }
}
