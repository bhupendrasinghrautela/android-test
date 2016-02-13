package com.makaan.response.listing;

import com.makaan.response.listing.Listing;

import java.util.ArrayList;

/**
 * Created by vaibhav on 24/12/15.

 */
public class ListingData {

    public String cityName;
    public int totalCount;
    //public int readableTotalCount;
    public ArrayList<Listing> listings = new ArrayList<>();
    public ListingFacets facets;

}
