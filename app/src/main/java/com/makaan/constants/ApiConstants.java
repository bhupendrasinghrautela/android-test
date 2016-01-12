package com.makaan.constants;

/**
 * Created by vaibhav on 29/12/15.
 */
public class ApiConstants {


    public static final String BASE_URL = "https://marketplace-qa.proptiger-ws.com";
    public static final String FILTER_GROUP = BASE_URL.concat("/data/v1/entity/filter-group");           //TODO: need to implement this on server side
    public static final String API_LABEL = BASE_URL.concat("/data/v1/entity/api-label");
    public static final String PROPERTY_STATUS = BASE_URL.concat("/data/v1/entity/property-status");
    public static final String UNIT_TYPE = BASE_URL.concat("/data/v1/entity/unit-types");

    public static final String LISTING = BASE_URL.concat("/app/v1/listing/");

    public static final String CITY_DATA = BASE_URL.concat("/data/v1/entity/city");
    public static final String CITY = BASE_URL.concat("/app/v1/city/");

    public static final String LOCALITY_DATA = BASE_URL.concat("/data/v3/entity/locality");
    public static final String LOCALITY = BASE_URL.concat("/app/v3/locality/");








   // public static final String CITY_SELECTOR = {"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}};
}
