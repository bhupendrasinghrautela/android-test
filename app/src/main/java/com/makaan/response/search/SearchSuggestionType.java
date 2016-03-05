package com.makaan.response.search;

/**
 * Created by sunil on 19/01/16.
 */
public enum SearchSuggestionType {

    //TODO

    BUILDER("BUILDER"),BUILDERCITY("BUILDERCITY"),
    PROJECT("PROJECT"), LOCALITY("LOCALITY"),
    SUBURB("SUBURB"), CITY("CITY"), GOOGLE_PLACE("GP"),
    PROJECT_SUGGESTION("Typeahead-Suggestion-Project"),
    SUGGESTION("Typeahead-Suggestion"), TEMPLATE("Typeahead-Template"),
    CITY_OVERVIEW("CITY-OVERVIEW"), LOCALITY_OVERVIEW("LOCALITY-OVERVIEW"),
    NEARBY_PROPERTIES("NEARBY-PROPERTIES"), HEADER_TEXT("HEADER-TEXT"),
    ERROR("ERROR"), SELLER("SELLER");

    private String value;

    SearchSuggestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
