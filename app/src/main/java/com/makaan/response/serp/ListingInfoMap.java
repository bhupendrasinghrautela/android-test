package com.makaan.response.serp;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 1/28/16.
 */
public class ListingInfoMap {
    public static final int MAP_BUY_PROPERTIES = 0;
    public static final int MAP_BUY_PLOT_PROPERTIES = 1;
    public static final int MAP_RENT_PROPERTIES = 2;

    public ArrayList<InfoMap> buyProperties;
    public ArrayList<InfoMap> plotBuyProperties;
    public ArrayList<InfoMap> rentProperties;

    public SparseArray<ArrayList<InfoMap>> map = new SparseArray<>();

    public void initialize() {
        map.put(MAP_BUY_PROPERTIES, buyProperties);
        map.put(MAP_BUY_PLOT_PROPERTIES, plotBuyProperties);
        map.put(MAP_RENT_PROPERTIES, rentProperties);
    }

    public class InfoMap {
        public String displayName;
        public String fieldName;
        public String imageName;
    }
}
