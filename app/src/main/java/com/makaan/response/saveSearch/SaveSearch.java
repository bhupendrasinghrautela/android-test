package com.makaan.response.saveSearch;

/**
 * Created by aishwarya on 26/01/16.
 */
public class SaveSearch {
    public Long id, userId, createdDate;
    public String searchQuery, name;
    public String jsonDump;

    public static class JSONDump {
        public String localityName;
        public String cityName;
        public String builderName;
        public String sellerName;
        public String priceRange;
        public String bhk;
        public String projectName;
    }
}
