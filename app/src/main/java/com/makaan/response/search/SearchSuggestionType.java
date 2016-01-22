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
    CITY_OVERVIEW("CITY_OVERVIEW"), LOCALITY_OVERVIEW("LOCALITY_OVERVIEW"),;

    private String value;

    SearchSuggestionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
