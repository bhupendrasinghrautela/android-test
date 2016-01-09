package com.makaan.activity.listing;

import com.makaan.event.serp.SerpGetEvent;
import com.makaan.response.listing.Listing;

/**
 * Created by rohitgarg on 1/6/16.
 * This interface will be implemented by Fragments
 * which want to receive communication from Housing/Parent activirty
 */
public interface ListingActivityCallbacks {
    /**
     * update new listings in the SerpListFragment
     * @param listingGetEvent Listing to be updated to the SerpListFragment
     */
    public void updateListings(SerpGetEvent listingGetEvent);
    /**
     * update new listings in the SerpListFragment
     * @param listing Listing to be updated to the SerpListFragment
     */
    public void updateListings(Listing listing);
}
