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

    public static ArrayList<GroupCluster> getGroupClusters(ArrayList<GroupListing> groupListings) {
        int size = groupListings.size();
        ArrayList<GroupCluster> clusters = new ArrayList<>();
        for(int i = 0; i < size; i += MAX_CLUSTERS_IN_GROUP) {
            GroupCluster cluster = new GroupCluster();
            for(int j = i; j <= ((i + (MAX_CLUSTERS_IN_GROUP - 1) < size) ? (i + (MAX_CLUSTERS_IN_GROUP - 1)) : (size - 1)); j++) {
                cluster.groupListings.add(groupListings.get(j));
            }
            clusters.add(cluster);
        }
        return clusters;
    }

    public static GroupCluster getGroupCluster(ArrayList<GroupListing> groupListings, Long childSerpId) {
        int start = -1;
        int end = -1;
        for(int i = 0; i < groupListings.size(); i++) {
            if(groupListings.get(i).listing.id == childSerpId) {
                start = i / MAX_CLUSTERS_IN_GROUP;
                end = start + MAX_CLUSTERS_IN_GROUP - 1;
                if(end > groupListings.size()) {
                    end = groupListings.size() - 1;
                }
                break;
            }
        }
        if(start != -1 && end != -1) {
            GroupCluster cluster = new GroupCluster();
            for(int i = start; i <= end; i++) {
                cluster.groupListings.add(groupListings.get(i));
            }
            return cluster;
        }
        return null;
    }
}
