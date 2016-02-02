package com.makaan.event.amenity;

import com.makaan.response.amenity.AmenityCluster;

import java.util.List;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenityGetEvent {
    public String message;
    public List<AmenityCluster> amenityClusters;

    public AmenityGetEvent(List<AmenityCluster> amenityClusters) {
        this.amenityClusters = amenityClusters;
    }

    public AmenityGetEvent() {
    }
}
