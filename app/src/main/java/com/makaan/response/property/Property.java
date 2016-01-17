package com.makaan.response.property;

import com.makaan.response.project.Project;

/**
 * Created by vaibhav on 17/01/16.
 */
public class Property {

    public Long propertyId, projectId;
    public int bedrooms, bathrooms, halls,storeRoom,studyRoom,poojaRoom,balcony;
    public boolean pentHouse, studio;
    public String unitType, measure;
    public double size;


    public Project project;

}
