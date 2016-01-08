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
    public static final String LISTING = BASE_URL.concat("/app/v1/listing");

    /*---Typeahead api constants*/
    public static final String TYPEAHEAD_BASE_URL = BASE_URL.concat("/columbus/app/v4/typeahead?");
    public static final String TYPEAHEAD_QUERY = "query=";
    public static final String TYPEAHEAD_TYPE = "typeAheadType=";
    public static final String TYPEAHEAD_ROWS = "&rows=5";
    public static final String TYPEAHEAD_ENHANCE_GP = "&enhance=gp";
    public static final String TYPEAHEAD_CITY = "&city=";




}
