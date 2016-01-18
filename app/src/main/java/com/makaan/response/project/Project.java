package com.makaan.response.project;

import com.makaan.response.image.Image;
import com.makaan.response.locality.Locality;

import java.util.ArrayList;

/**
 * Created by vaibhav on 24/12/15.
 */
public class Project {


    private String fullName;


    public String name, url;
    public String builderName;          //used may be only in serp listing parse
    public boolean actual;
    public Long projectId;
    public boolean isHotProject,has3DImages;

    public Locality locality;
    public Builder builder;
    public int projectTypeId, minBedrooms, macBedRooms;

    public String imageURL;
    public Double latitude, longitude;
    public String projectStatus;

    public String description, propertySizeMeasure;
    public Double avgPriceRisePercentage,derivedAvailability,livabilityScore;
    public ArrayList<Image> images = new ArrayList<>();


    public String getFullName(){
        if(null != fullName){
            return fullName;
        }else if(null != builderName && null != name){
            return builderName.concat(" ").concat(name);
        }
        else if(null != name && null != builder && null != builder.name){
            return builder.name.concat(" ").concat(name);
        }
        return null;
    }


}
