package com.makaan.response.project;

import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.response.image.Image;
import com.makaan.response.locality.Locality;
import com.makaan.response.property.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 24/12/15.
 */
public class Project {


    private String fullName, completeProjectAddress, paymentPlanUrl;

    public String address,dominantUnitType;
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
    //public ArrayList<ProjectSpecification> resiProjectSpecifications;

    public HashMap<String, Object> specifications;
    public String activeStatus;
    public Long preLaunchDate;


    public HashMap<String, ArrayList<SpecificaitonsUI>> getFormattedSpecifications() {
        if(specifications == null){
            return null;
        }
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

    public String getFullName() {
        if (null != builderName && null != name) {
            return builderName.concat(" ").concat(name);
        } else if (null != name && null != builder && null != builder.name) {
            return builder.name.concat(" ").concat(name);
        } else if (null != fullName) {
            return fullName;
        } else if (null != name) {
            return name;
        }
        return null;
    }


    public String getMinPriceOnwards() {
        if(minPrice == null)
            return "";
        else
            return "\u20B9 "+(String.format("%.1f", minPrice/100000))+" L onwards";
    }

}
