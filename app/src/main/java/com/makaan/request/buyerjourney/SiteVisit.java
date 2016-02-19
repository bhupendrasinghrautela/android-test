package com.makaan.request.buyerjourney;

import java.util.ArrayList;

/**
 * Created by aishwarya on 19/02/16.
 */
public class SiteVisit {
    public Long agentId;
    public int eventTypeId;
    public Long performTime;
    public ArrayList<ListingDetails> listingDetails;
    public ArrayList<Long> projectIds;

    public class ListingDetails {
        public Long listingId;
    }
}
