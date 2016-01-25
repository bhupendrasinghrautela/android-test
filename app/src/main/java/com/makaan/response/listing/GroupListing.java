package com.makaan.response.listing;

import com.makaan.response.project.Project;

import java.util.ArrayList;
import java.util.HashMap;

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
