package com.makaan.response.listing.detail;

import com.makaan.response.property.Property;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ListingDetail {


    public Long id;
    public double latitude,longitude;
    public CurrentListingPrice currentListingPrice;
    public String description,listingCategory;
    public String expiryAt,possessionDate;
    public int facingId;
    public boolean status;
    public int noOfCarParks;
    public int propertyId;


    public double avgPriceRisePercentage, bookingAmount,mainEntryRoadWidth;
    public int bookingStatusId,ownershipTypeId;
    public String furnished;
    public int sellerId, totalFloors, floor;
    public ArrayList<Furnishing> furnishings = new ArrayList<>();

    public ArrayList<Long> amenitiesIds = new ArrayList<>();


    public double securityDeposit;
    public Property property;

    public ArrayList<HashMap<String,String>> viewDirections;

    public String getDescription() {
        //return AppUtils.stripHtml(description);
        return description;
    }
}
