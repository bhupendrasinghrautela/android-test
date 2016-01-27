package com.makaan.response.project;

import com.makaan.response.image.Image;

import java.util.ArrayList;

/**
 * Created by vaibhav on 17/01/16.
 */
public class Builder {

    public Long id;
    public String name, imageURL, description, activeStatus,builderWebsite,builderAddress;

    public Double projectCount,builderLocalityCount,builderScore;
    public String establishedDate;


    public ArrayList<Image> images = new ArrayList<>();
    public Image mainImage;
    public ProjectStatusCount projectStatusCount;



}
