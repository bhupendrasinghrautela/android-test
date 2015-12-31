package com.makaan.response.serp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vaibhav on 24/12/15.
 */
public class Listing {

    /**
     * on simple card
     * priceInWords, unitInfo
     * bhkInfo, sizeInfo
     * project name, locality name, suburbName, cityName
     * listing posted by name (listing posted by type)
     * relativeCreateDate
     * posted by rating
     */
    public String priceInWords, unitInfo;
    public String bhkInfo, sizeInfo;

    public String postedDate;
    public Integer lisitingId;
    public String description;
    public Integer bedrooms, bathrooms;

    public String propertyType;         //like apartment etc.
    public Integer size;
    public String measure;

    public Double latitude, longitude;
    public Integer studyRoom, servantRoom, poojaRoom;
    public Integer pricePerUnitArea;
    public Double price, localityAvgPrice;
    public String relativeCreateDate;
    public String propertyStatus;
    public boolean isReadyToMove;
    public String propertyAge;
    public String possessionDate;
    public Double listingPriceDiff;
    public String localityName, suburbName, cityName;

    public LisitingPostedBy lisitingPostedBy;

    public boolean hasOffer;
    public Integer floor, totalFloors;
    public String facing;
    public String furnished;

    public ArrayList<ListingImage> images = new ArrayList<>();

    public Project project ;

    public Long landMarkDistance;

    public ArrayList<SimilarListing> similarListings = new ArrayList<>();


}
