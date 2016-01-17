package com.makaan.event.listing;

import com.makaan.event.MakaanEvent;
import com.makaan.response.listing.detail.ListingDetail;

/**
 * Created by vaibhav on 07/01/16.
 */
public class ListingByIdGetEvent extends MakaanEvent {
    public ListingDetail listingDetail;

    public ListingByIdGetEvent(ListingDetail listingDetail) {
        this.listingDetail = listingDetail;
    }

    public ListingByIdGetEvent() {
    }
}
