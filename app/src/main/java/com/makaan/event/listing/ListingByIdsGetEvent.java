package com.makaan.event.listing;

import com.makaan.event.MakaanEvent;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.detail.ListingDetail;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 07/01/16.
 */
public class ListingByIdsGetEvent extends MakaanEvent {
    public ArrayList<Listing> items;

    public ListingByIdsGetEvent() {
    }
    public class Listing {
        public ListingDetail listing;
    }
}
