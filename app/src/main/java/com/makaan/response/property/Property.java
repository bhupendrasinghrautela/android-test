package com.makaan.response.property;

import com.makaan.response.project.Project;

import java.util.HashMap;

/**
 * Created by vaibhav on 17/01/16.
 */
public class Property {

    public Long propertyId, projectId;
    public boolean pentHouse, studio;
    public String unitType, measure;

    public Double size,budget,minResaleOrPrimaryPrice,maxResaleOrPrimaryPrice;
    public Integer bedrooms, bathrooms, balcony,storeRoom, halls,studyRoom,servantRoom, poojaRoom;
    public HashMap<String, Integer> imageTypeCount;
    public Project project;

}
