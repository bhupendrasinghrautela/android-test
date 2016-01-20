package com.makaan.event.locality;

import com.makaan.event.MakaanEvent;
import com.makaan.response.locality.Locality;

import java.util.ArrayList;

/**
 * Created by vaibhav on 19/01/16.
 */
public class NearByLocalitiesEvent extends MakaanEvent{

    public ArrayList<Locality> nearbyLocalities ;

    public NearByLocalitiesEvent(ArrayList<Locality> nearbyLocalities) {
        this.nearbyLocalities = nearbyLocalities;
    }

    public NearByLocalitiesEvent() {
    }
}
