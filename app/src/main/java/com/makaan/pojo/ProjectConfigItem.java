package com.makaan.pojo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vaibhav on 24/01/16.
 */
public class ProjectConfigItem {

    public double minPrice, maxPrice;
    public Set<Integer> bedrooms = new HashSet<>();
    public int propertyCount;
    public int sellerCount;

    public HashMap<Long, SellerCard> companies = new HashMap<>();

}
