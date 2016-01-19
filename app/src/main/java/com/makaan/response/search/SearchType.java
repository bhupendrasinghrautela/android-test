package com.makaan.response.search;

/**
 * Created by sunil on 07/01/16.
 */
public enum SearchType {
    //TODO this should be kept in a config file

    ALL(""), PROJECT("PROJECT"), LOCALITY("LOCALITY"), BUILDER_CITY("BUILDER-CITY");
    private String value;

    SearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
