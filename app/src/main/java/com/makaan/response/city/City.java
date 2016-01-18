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
    public String label;
    public String cityTagLine;

    public String cityHeroshotImageUrl;
    public Double avgPricePerUnitArea, averageRentPerMonth;


    public ArrayList<EntityDesc> entityDescriptions = new ArrayList<>();
}
