package com.makaan.analytics;

/**
 * Created by sunil on 17/02/16.
 */
public class MakaanTrackerConstants {

    public static final String segmentSdkKey = "xMHqomsTMdGwOiJByBsKUbH78Akhbaku";

    public static final String SCREEN_NAME = "Screen name";
    public static final String KEYWORD = "Keywords";
    public static final String LISTING_POSITION = "Listing position";

    public enum Category{
        property("Buyer_Property");

        private String value;

        private Category(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Action{

        searchPropertyBuy("SEARCH_Property_Buy");

        private String value;

        private Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }
}
