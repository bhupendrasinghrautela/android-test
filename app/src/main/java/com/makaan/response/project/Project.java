package com.makaan.response.project;

import com.makaan.response.image.Image;
import com.makaan.response.locality.Locality;
import com.makaan.response.property.Property;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vaibhav on 24/12/15.
 */
public class Project {


    private String fullName, address, completeProjectAddress, paymentPlanUrl;


    public String name, url, newsTag;
    public String builderName;          //used may be only in serp listing parse
    public boolean actual;
    public Long projectId, localityId, builderId;
    public boolean isHotProject, has3DImages, resaleEnquiry, hasTownship, hasProjectInsightReport;
    public String launchDate, possessionDate, submittedDate, createdDate;

    public Locality locality;
    public Builder builder;

    public Image mainImage;

    public String imageURL;
    public Double latitude, longitude, minPricePerUnitArea, maxPricePerUnitArea, minSize, maxSize, minPrice, maxPrice, sizeInAcres,
            avgPriceRisePercentage, avgPriceRiseMonths, supply, derivedAvailability,
            projectLocalityScore, safetyScore, projectSocietyScore, projectSafetyRank, projectLivabilityRank,
            livabilityScore, avgPricePerUnitArea, minResaleOrPrimaryPrice, maxResaleOrPrimaryPrice,
            minDiscountPrice, maxDiscountPrice, minResaleOrDiscountPrice, maxResaleOrDiscountPrice, avgFlatsPerFloor, delayInMonths;
    public Integer minBedrooms, maxBedrooms, projectTypeId, imagesCount, videosCount;
    public HashMap<String, Integer> imageCountByType;
    public String projectStatus;
    public ArrayList<Integer> distinctBedrooms;

    public boolean isResale, isPrimary, isSoldOut;
    public String description, propertySizeMeasure;
    public ArrayList<Image> images = new ArrayList<>();

    public ArrayList<Property> properties = new ArrayList<>();
    public ArrayList<ProjectAmenity> projectAmenities = new ArrayList<>();
    public ArrayList<ProjectSpecification> resiProjectSpecifications;

    public String getFullName() {
        if (null != fullName) {
            return fullName;
        } else if (null != builderName && null != name) {
            return builderName.concat(" ").concat(name);
        } else if (null != name && null != builder && null != builder.name) {
            return builder.name.concat(" ").concat(name);
        }
        return null;
    }


}
