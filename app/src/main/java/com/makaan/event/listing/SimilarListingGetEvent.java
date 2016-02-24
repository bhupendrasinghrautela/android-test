package com.makaan.event.listing;

import com.makaan.event.MakaanEvent;
import com.makaan.response.listing.detail.ListingDetail;

import java.util.ArrayList;

/**
 * Created by aishwarya on 24/02/16.
 */
public class SimilarListingGetEvent extends MakaanEvent {
    public SimilarListingData data;

    public class SimilarListingData {
        public ArrayList<ListingItems> items;
    }

    public class ListingItems {
        public ListingDetail listing;
    }
}
