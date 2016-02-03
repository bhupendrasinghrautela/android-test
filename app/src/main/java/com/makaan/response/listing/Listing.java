package com.makaan.response.listing;

import com.makaan.response.project.Project;

import java.util.ArrayList;
import java.util.HashMap;

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
    public Long id;
    public String priceInWords, unitInfo;
    public String bhkInfo, sizeInfo;
    public int imageCount;

    public String unitType,unitName;

    public String postedDate, mainImageUrl;
    public Integer lisitingId,propertyId,projectId;
    public Long sellerId;
    public String description;
    public Integer bedrooms, bathrooms, balcony,storeRoom, halls,studyRoom,servantRoom, poojaRoom;

    public String propertyType;         //like apartment etc.
    public Double size,budget,minResaleOrPrimaryPrice,maxResaleOrPrimaryPrice;
    public String measure;

    public Double latitude, longitude;

    public Integer pricePerUnitArea;
    public Double price, localityAvgPrice;
    public String relativeCreateDate;
    public String propertyStatus;
    public boolean isReadyToMove;
    public String propertyAge;
    public String possessionDate;
    public Double listingPriceDiff;
    public String localityName, suburbName, cityName;
    public String listingCategory;

    public LisitingPostedBy lisitingPostedBy;

    public boolean hasOffer;
    public Integer floor, totalFloors;
    public String facing;
    public String furnished;
    public String ownershipType;
    public Integer noOfOpenSides;
    public Integer securityDeposit;

    public ArrayList<ListingImage> images = new ArrayList<>();

    public Project project ;

    public Long landMarkDistance;


    public long minConstructionCompletionDate;
    public long maxConstructionCompletionDate;
}
