package com.makaan.pojo;

import java.io.Serializable;

/**
 * Created by vaibhav on 25/01/16.
 */
public class SellerCard implements Serializable,Comparable<SellerCard> {
    public Long userId;
    public Long sellerId;
    public Double rating;
    public boolean assist;
    public boolean isChecked;
    public String contactNo;
    public String imageUrl;
    public Long noOfProperties;
    public String name;
    public String type;

    @Override
    public int compareTo(SellerCard another) {
        if (this.rating != null && another.rating != null) {
            if (this.rating - another.rating > 0) {
                return -1;
            } else if (this.rating - another.rating < 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (this.rating != null) {
            return -1;
        } else if (another.rating != null) {
            return 1;
        }
        return 0;
    }
}

