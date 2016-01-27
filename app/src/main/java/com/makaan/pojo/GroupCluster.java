package com.makaan.pojo;

import com.makaan.response.listing.GroupListing;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 1/27/16.
 */
public class GroupCluster {
    public ArrayList<GroupListing> groupListings = new ArrayList<>();
    public static final int MAX_CLUSTERS_IN_GROUP = 3;
    public static final int CLUSTER_POS_IN_SERP_PER_TEN = 3;

    public static ArrayList<GroupCluster> getGroupCluster(ArrayList<GroupListing> groupListings) {
        int size = groupListings.size();
        ArrayList<GroupCluster> clusters = new ArrayList<>();
        for(int i = 0; i < size; i += MAX_CLUSTERS_IN_GROUP) {
            GroupCluster cluster = new GroupCluster();
            cluster.groupListings = groupListings;
            for(int j = i; j <= ((i + (MAX_CLUSTERS_IN_GROUP - 1) < size) ? (i + (MAX_CLUSTERS_IN_GROUP - 1)) : (size - 1)); j++) {
                cluster.groupListings.add(groupListings.get(i));
            }
        }
        return clusters;
    }
}
