package com.makaan.request.buyerjourney;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class AgentRating {
    public long sellerId;
    public int rating;
    public long listingId;
    public String listingName;
    public String localityName;
    public long cityId;
    public String reviewComment;
    public ArrayList<SellerRatingParameter> sellerRatingParameters;
    public class SellerRatingParameter {
        public SellerRatingParameter() {}
        public SellerRatingParameter(int id) {
            this.id = id;
        }
        int id;
    }
}