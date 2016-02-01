package com.makaan.constants;

/**
 * Created by vaibhav on 29/12/15.
 */
public class ApiConstants {


//    public static final String BASE_URL = "https://marketplace-qa.proptiger-ws.com";
    public static final String BASE_URL = "http://marketplace-qa.makaan-ws.com";
//    public static final String BASE_URL = "http://mp-qa1.makaan-ws.com";
    //public static final String BASE_URL = "http://mp-qa1.makaan-ws.com/";
    public static final String FILTER_GROUP = BASE_URL.concat("/data/v1/entity/filter-group");           //TODO: need to implement this on server side
    public static final String LISTING_INFO_MAP = BASE_URL.concat("/data/v1/entity/listing-info-map");           //TODO: need to implement this on server side
    public static final String API_LABEL = BASE_URL.concat("/data/v1/entity/api-label");
    public static final String PROPERTY_STATUS = BASE_URL.concat("/data/v1/entity/property-status");
    public static final String PROPERTY_DISPLAY_ORDER = BASE_URL.concat("/data/v1/entity/property-display-order");
    public static final String DEFAULT_AMENITY = BASE_URL.concat("/data/v1/entity/default-amenity");
    public static final String UNIT_TYPE = BASE_URL.concat("/data/v1/entity/unit-types");
    public static final String AMENITY = BASE_URL.concat("/data/v1/entity/amenities");
    public static final String JARVIS_MESSAGE_TYPE = BASE_URL.concat("/data/v1/entity/jarvis/message-type");
    public static final String JARVIS_CTA_MESSAGE_TYPE = BASE_URL.concat("/data/v1/entity/jarvis/cta-message-type");
    public static final String SEARCH_TYPE = BASE_URL.concat("/data/v1/entity/search-types");
    public static final String PROPERTY_AMENITY = BASE_URL.concat("/data/v1/entity/master-amenities");
    public static final String MASTER_FURNISHINGS = BASE_URL.concat("/data/v1/entity/master-furnishings");
    public static final String MASTER_SPECIFICATIONS = BASE_URL.concat("/data/v1/entity/master-specifications?filters=masterSpecificationCategory.masterSpecParentCat.masterSpecParentCatId==1&fields=masterSpecId,masterSpecificationCategory,masterSpecClassName,masterSpecCatId,masterSpecCatName,masterSpecCatDisplayName,masterSpecParentCat,masterSpecParentCatId,masterSpecParentDisplayName,masterSpecificationCategory.masterSpecParentCat,masterSpecificationCategory.masterSpecParentCat.masterSpecParentCatId,masterSpecificationCategory.masterSpecParentCat.masterSpecParentDisplayName");
    public static final String DIRECTIONS = BASE_URL.concat("/data/v1/entity/direction");
    public static final String OWNERSHIP_TYPE = BASE_URL.concat("/data/v1/entity/master-ownership-types");

    public static final String COMPANY_USERS = BASE_URL.concat("/userservice/data/v1/entity/company-users");
    public static final String IMAGE = BASE_URL.concat("/data/v4/entity/image");


    public static final String LISTING = BASE_URL.concat("/app/v1/listing/");
    public static final String SIMILAR_LISTING = BASE_URL.concat("/app/v1/similar/listing/");

    public static final String CITY_DATA = BASE_URL.concat("/data/v1/entity/city");
    public static final String CITY = BASE_URL.concat("/app/v1/city/");

    public static final String LOCALITY_DATA = BASE_URL.concat("/data/v3/entity/locality");
    public static final String LOCALITY = BASE_URL.concat("/app/v3/locality/");

    public static final String TREND_URL = BASE_URL.concat("/data/v1/trend/hitherto");
    public static final String LOCALITY_TREND_URL = TREND_URL.concat("?fields=minPricePerUnitArea,localityName,projectName&group=localityId,month");
    public static final String PROJECT_TREND_URL = TREND_URL.concat("?fields=minBuyPricePerUnitArea,localityName,projectName&group=projectId,month");

    public static final String SAVED_SEARCH_URL = BASE_URL.concat("/data/v1/entity/user/saved-searches");
    public static final String SAVE_NEW_SEARCH_URL = BASE_URL.concat("data/v1/entity/saved-searches");
    public static final String TOP_AGENTS_CITY = BASE_URL.concat("/data/v1/entity/city/");
    public static final String TOP_AGENTS = "/top-agents";
    public static final String PYR = BASE_URL.concat("/data/v1/entity/enquiry");
    public static final String TOP_BUILDER = BASE_URL.concat("/data/v2/entity/builder/top");
    public static final String BUILDER_DETAIL = BASE_URL.concat("/app/v1/builder-detail");
    public static final String SELLER_DETAIL = BASE_URL.concat("/app/v1/company");
    public static final String GP_DETAIL = BASE_URL.concat("/app/v1/gp/place-detail");
    public static final String PROJECT = BASE_URL.concat("/app/v4/project-detail");
    public static final String SIMILAR_PROJECT = BASE_URL.concat("/data/v2/recommendation");
    public static final String PROJECT_CONFIG = BASE_URL.concat("/app/v1/project-configuration");

    public static final String BLOG_URL = BASE_URL.concat("/data/v2/entity/blog");


    public static final String LISTING_OTHER_SELLERS = BASE_URL.concat("/data/v2/entity/domain");
    public static final String LEAD_INSTANT_CALLBACK_URL= BASE_URL.concat("/app/v1/callNow");
    public static final String COLUMBUS_SUGGESTIONS = BASE_URL.concat("/columbus/app/v1/popular/suggestions");

    //public static final String SIMILAR_LISTING = BASE_URL.concat("/app/v1/similar/listing/");

    // public static final String CITY_SELECTOR = {"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}};
}
