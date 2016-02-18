package com.makaan.response.buyerjourney;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class AgentRating {
    long id;
    long sellerId;
    long userId;
    int rating;
    int ratingScale;
    String reviewComment;
    Long createdAt;
    ArrayList<SellerRatingParameter> sellerRatingParameters;
    public class SellerRatingParameter {
        SellerRatingParameter() {}
        SellerRatingParameter(int id) {
            this.id = id;
        }
        int id;
        String name;
    }
}