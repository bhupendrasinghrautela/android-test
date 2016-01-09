package com.makaan.response.city;

import java.util.ArrayList;

/**
 * Created by vaibhav on 09/01/16.
 */
public class City {

    public Long id;
    public Double centerLatitude;
    public Double centerLongitude, annualGrowth, rentalYield, demandRate, supplyRate;
    public String description;

    public String cityHeroshotImageUrl;


    public ArrayList<CityEntityDesc> entityDescriptions = new ArrayList<>();
}
