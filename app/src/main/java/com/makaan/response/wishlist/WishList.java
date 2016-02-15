package com.makaan.response.wishlist;

import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.project.Project;

/**
 * Created by sunil on 25/01/16.
 */
public class WishList {
    public Long listingId, projectId, wishListId;
    public String cityLabel, unitName, builderName, projectName;
    public Project project;
    public ListingDetail listing;
}
