package com.makaan.constants;

/**
 * Created by vaibhav on 29/12/15.
 */
public class ApiConstants {
    public static final String SOURCE_DOMAIN_MAKAAN = "&sourceDomain=Makaan";

    public static final String BASE_URL = "https://marketplace-qa.proptiger-ws.com";
    public static final String FILTER_GROUP = BASE_URL.concat("/data/v1/entity/filter-group");           //TODO: need to implement this on server side
    public static final String API_LABEL = BASE_URL.concat("/data/v1/entity/api-label");
    public static final String PROPERTY_STATUS = BASE_URL.concat("/data/v1/entity/property-status");
    public static final String UNIT_TYPE = BASE_URL.concat("/data/v1/entity/unit-types");
    public static final String AMENITY = BASE_URL.concat("/data/v1/entity/amenities");
    public static final String SEARCH_TYPE = BASE_URL.concat("/data/v1/entity/search-types");
    public static final String PROPERTY_AMENITY = BASE_URL.concat("/data/v1/entity/master-amenities");
    public static final String MASTER_FURNISHINGS = BASE_URL.concat("/data/v1/entity/master-furnishings");

    public static final String COMPANY_USERS = BASE_URL.concat("/userservice/data/v1/entity/company-users");
    public static final String LISTING_IMAGE = BASE_URL.concat("/data/v1/entity/image?objectType=listing&objectId=");

    public static final String LISTING = BASE_URL.concat("/app/v1/listing/");

    public static final String CITY_DATA = BASE_URL.concat("/data/v1/entity/city");
    public static final String CITY = BASE_URL.concat("/app/v1/city/");

    public static final String LOCALITY_DATA = BASE_URL.concat("/data/v3/entity/locality");
    public static final String LOCALITY = BASE_URL.concat("/app/v3/locality/");

    public static final String TREND_URL = BASE_URL.concat("/data/v1/trend/hitherto");
    public static final String LOCALITY_TREND_URL = TREND_URL.concat("?fields=minPricePerUnitArea,localityName,projectName&group=localityId,month");

    public static final String TOP_AGENTS_LOCALITY = BASE_URL.concat("/data/v1/entity/city");
    public static final String TOP_AGENTS_URL = BASE_URL.concat("/data/v1/entity/city/");
    public static final String TOP_AGENTS = "/top-agents?";
    public static final String PYR = BASE_URL.concat("/data/v1/entity/enquiry");


    public static final String COLUMBUS_SUGGESTIONS = BASE_URL.concat("/columbus/app/v1/popular/suggestions");

    // public static final String CITY_SELECTOR = {"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}};
}
