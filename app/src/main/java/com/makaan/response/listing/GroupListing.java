package com.makaan.response.listing;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 24/12/15.
 */
public class GroupListing {
    public Listing listing;
    public ArrayList<GroupingAttributes> groupingAttributes;
    public ArrayList<GroupedListings> groupedListings;

    public class Listing {
        public Long id;
    }

    public class GroupingAttributes {
        public String type;
        public String value;
    }

    class GroupedListings {
        public Long id;
    }
}
