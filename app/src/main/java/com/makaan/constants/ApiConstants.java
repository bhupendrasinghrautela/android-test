package com.makaan.constants;

/**
 * Created by vaibhav on 29/12/15.
 */
public class ApiConstants {


    //public static final String BASE_URL = "https://mp-qa2.makaan-ws.com";
    public static final String BASE_URL = "http://www.makaan.com";
    //public static final String BASE_URL = "https://mp-qa1.makaan-ws.com";
    //public static final String BASE_URL = "https://preprod.makaan-ws.com";

    //public static final String BASE_URL = "https://beta.makaan-ws.com";
    private static final String MICROSERVICE_MADELYNE = BASE_URL.concat("/madelyne");
    private static final String MICROSERVICE_COMPASS = BASE_URL.concat("/compass");
    private static final String MICROSERVICE_PIXIE = BASE_URL.concat("/pixie");
    private static final String MICROSERVICE_PETRA = BASE_URL.concat("/petra");
    private static final String MICROSERVICE_COLUMBUS = BASE_URL.concat("/columbus");
    private static final String MICROSERVICE_MADROX = BASE_URL.concat("/madrox");
    private static final String MICROSERVICE_DOWNSTAR = BASE_URL.concat("/dawnstar");
    private static final String MICROSERVICE_SAPPHIRE = BASE_URL.concat("/sapphire");
    private static final String MICROSERVICE_KIRA = BASE_URL.concat("/kira");
    public static final String FILTER_GROUP = BASE_URL.concat("/petra/data/v1/entity/filter-group");           //TODO: need to implement this on server side
    public static final String LISTING_INFO_MAP = BASE_URL.concat("/petra/data/v1/entity/listing-info-map");           //TODO: need to implement this on server side
    public static final String API_LABEL = BASE_URL.concat("/petra/data/v1/entity/api-label");
    public static final String PROPERTY_STATUS = BASE_URL.concat("/petra/data/v1/entity/property-status");
    public static final String PROPERTY_DISPLAY_ORDER = BASE_URL.concat("/petra/data/v1/entity/property-display-order");
    public static final String DEFAULT_AMENITY = BASE_URL.concat("/petra/data/v1/entity/default-amenity");
    public static final String UNIT_TYPE = BASE_URL.concat("/petra/data/v1/entity/unit-types");
    public static final String AMENITY_LOCALITY = BASE_URL.concat("/petra/data/v1/entity/amenities/locality");
    public static final String AMENITY_PROJECT = BASE_URL.concat("/petra/data/v1/entity/amenities/project");
    public static final String CONSTRUCTION_STATUS = BASE_URL.concat("/petra/data/v1/entity/master-listing-construction-status");
    public static final String JARVIS_MESSAGE_TYPE = BASE_URL.concat("/petra/data/v1/entity/jarvis/message-type");
    public static final String JARVIS_CTA_MESSAGE_TYPE = BASE_URL.concat("/petra/data/v1/entity/jarvis/cta-message-type");
    public static final String JARVIS_SERP_FILTER_MESSAGE_MAP = BASE_URL.concat("/petra/data/v1/entity/jarvis/serp_filter-message-message");
    public static final String SEARCH_TYPE = BASE_URL.concat("/petra/data/v1/entity/search-types");
    public static final String PROPERTY_AMENITY = BASE_URL.concat("/petra/data/v1/entity/master-amenities");
    public static final String MASTER_FURNISHINGS = BASE_URL.concat("/petra/data/v1/entity/master-furnishings");
    public static final String MASTER_SPECIFICATIONS = BASE_URL.concat("/petra/data/v1/entity/master-specifications?filters=masterSpecificationCategory.masterSpecParentCat.masterSpecParentCatId==1&fields=masterSpecId,masterSpecificationCategory,masterSpecClassName,masterSpecCatId,masterSpecCatName,masterSpecCatDisplayName,masterSpecParentCat,masterSpecParentCatId,masterSpecParentDisplayName,masterSpecificationCategory.masterSpecParentCat,masterSpecificationCategory.masterSpecParentCat.masterSpecParentCatId,masterSpecificationCategory.masterSpecParentCat.masterSpecParentDisplayName");
    public static final String DIRECTIONS = BASE_URL.concat("/petra/data/v1/entity/direction");
    public static final String OWNERSHIP_TYPE = BASE_URL.concat("/petra/data/v1/entity/master-ownership-types");
    //TODO change path
    public static final String VERSION_UPDATE_URL = "http://content.makaan.com.s3.amazonaws.com/app/assets/buyer/versionUpdate.json";

    public static final String COMPANY_USERS = BASE_URL.concat("/madrox/data/v1/entity/company-users");
    public static final String IMAGE = MICROSERVICE_PETRA.concat("/data/v4/entity/image");
    public static final String HOSTED_IMAGE_URL = "http://content.makaan.com.s3.amazonaws.com/app/assets/buyer/icons";


    public static final String LISTING = MICROSERVICE_PETRA.concat("/app/v1/listing/");
    public static final String MY_LOCATION = MICROSERVICE_PETRA.concat("/app/v1/mylocation");
    public static final String SIMILAR_LISTING = MICROSERVICE_PETRA.concat("/app/v1/similar/listing/");

    public static final String CITY = MICROSERVICE_PETRA.concat("/app/v1/city/");

    public static final String LOCALITY_DATA = MICROSERVICE_PETRA.concat("/data/v3/entity/locality");
    public static final String LOCALITY = MICROSERVICE_PETRA.concat("/app/v3/locality/");

    public static final String TREND_URL = MICROSERVICE_COMPASS.concat("/data/v1/trend");
    public static final String LOCALITY_TREND_URL = TREND_URL.concat("?fields=avgBuyPricePerUnitArea,localityName&group=localityId,month");
    public static final String CITY_TREND_URL = TREND_URL.concat("?fields=avgBuyPricePerUnitArea&group=month");
    public static final String PROJECT_TREND_URL = TREND_URL.concat("?fields=avgBuyPricePerUnitArea&group=month");

    public static final String SAVED_SEARCH_URL = MICROSERVICE_PETRA.concat("/data/v1/entity/user/saved-searches/");
    public static final String SAVE_NEW_SEARCH_URL = MICROSERVICE_PETRA.concat("/data/v1/entity/saved-searches");
    public static final String SAVED_SEARCH_NEW_MATCHES_URL = MICROSERVICE_PETRA.concat("/data/v1/entity/user/saved-searches/new-matches");
    public static final String TOP_AGENTS_CITY = MICROSERVICE_PETRA.concat("/data/v1/entity/city/");
    public static final String TOP_AGENTS = "/top-agents";
    public static final String PYR = BASE_URL.concat("/petra/data/v1/entity/enquiry");
    public static final String TOP_BUILDER = MICROSERVICE_PETRA.concat("/data/v2/entity/builder/top");
    public static final String BUILDER_DETAIL = MICROSERVICE_PETRA.concat("/app/v1/builder-detail");
    public static final String SELLER_DETAIL = MICROSERVICE_PETRA.concat("/app/v1/company");
    public static final String GP_DETAIL = MICROSERVICE_PETRA.concat("/app/v1/gp/place-detail");
    public static final String PROJECT = MICROSERVICE_PETRA.concat("/app/v4/project-detail");
    public static final String SIMILAR_PROJECT = MICROSERVICE_PETRA.concat("/data/v2/recommendation");
    public static final String PROJECT_CONFIG = MICROSERVICE_PETRA.concat("/app/v1/project-configuration");
    public static final String AMENITIES = MICROSERVICE_PETRA.concat("/app/v1/amenity?latitude=");

    public static final String BLOG_URL = "http://makaaniq.com/blog?sort=views-desc&tag=";


    public static final String LISTING_OTHER_SELLERS = MICROSERVICE_PETRA.concat("/data/v2/entity/domain");
    public static final String LEAD_INSTANT_CALLBACK_URL= MICROSERVICE_KIRA.concat("/app/v1/callNow");
    public static final String COLUMBUS_SUGGESTIONS = MICROSERVICE_COLUMBUS.concat("/app/v1/popular/suggestions");
    public static final String APP_V1 = "/app/v1";
    public final static int CONNECTION_TIMEOUT = 30000;
    /*public static final String FORGOT_PASSWORD = "/app/v1/reset-password?email=";*/
    public static final String FORGOT_PASSWORD = MICROSERVICE_MADROX.concat("/app/v1/reset-password");
    public static final String GOOGLE_NAV_BASE_STRING = "http://maps.google.com/?daddr=";
    public static final String ICRM_CLIENT_LEADS = MICROSERVICE_SAPPHIRE.concat("/v1/client-leads");
    public static final String USER_SERVICE_ENTITY_COMPANIES = MICROSERVICE_MADROX.concat("/data/v1/entity/companies?filters=");
    public static final String USER_SERVICE_ENTITY_COMPANIES_FILTER = "id==%d,";
    public static final String SELLER_RATING = MICROSERVICE_PETRA.concat("/data/v1/entity/user/seller-rating-review");
    public static final String SITE_VISIT_CLIENT_EVENTS = MICROSERVICE_SAPPHIRE.concat("/v1/client/events/");
    public static final String GCM = MICROSERVICE_MADROX.concat("/data/v1/entity/gcm-user");
    public static final String TYPEAHEAD = MICROSERVICE_COLUMBUS.concat("/app/v5/typeahead?");
    public static final String WISHLIST = MICROSERVICE_PETRA.concat("/data/v1/entity/user/wish-list");
    public static final String WISHLIST_V2 = MICROSERVICE_PETRA.concat("/data/v2/entity/user/123/wish-list"); //dummy user id is ignored at server but required in url;
    public static final String FACEBOOK_LOGIN = MICROSERVICE_MADROX.concat("/app/v1/login/facebook?access_token=");
    public static final String GOOGLE_LOGIN = MICROSERVICE_MADROX.concat("/app/v1/login/google?access_token=");
    public static final String MAKAAN_LOGIN = MICROSERVICE_MADROX.concat("/app/v1/login");
    public static final String LOGOUT = MICROSERVICE_MADROX.concat("/app/v1/logout");
    public static final String REGISTER = MICROSERVICE_MADROX.concat("/app/v1/register");
    public static final String PROPERTY_REQUIREMENTS = MICROSERVICE_SAPPHIRE.concat("/v1/property-requiements");

    //public static final String SIMILAR_LISTING = BASE_URL.concat("/app/v1/similar/listing/");

    // public static final String CITY_SELECTOR = {"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}};
}
