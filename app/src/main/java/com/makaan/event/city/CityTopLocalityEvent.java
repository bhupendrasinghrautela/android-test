package com.makaan.event.city;

import com.makaan.event.MakaanEvent;
import com.makaan.response.locality.Locality;

import java.util.ArrayList;

/**
 * Created by vaibhav on 11/01/16.
 */
public class CityTopLocalityEvent extends MakaanEvent {

    public ArrayList<Locality> topLocalitiesInCity = new ArrayList<>();

    public CityTopLocalityEvent(ArrayList<Locality> topLocalitiesInCity) {
        this.topLocalitiesInCity = topLocalitiesInCity;
    }

    public CityTopLocalityEvent() {
    }
}
