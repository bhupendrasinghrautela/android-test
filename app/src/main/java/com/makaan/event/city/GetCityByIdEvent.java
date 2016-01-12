package com.makaan.event.city;

import com.makaan.event.MakaanEvent;
import com.makaan.response.city.City;

/**
 * Created by vaibhav on 09/01/16.
 */
public class GetCityByIdEvent extends MakaanEvent {

    public City city;

    public GetCityByIdEvent(City city) {
        this.city = city;
    }

    public GetCityByIdEvent() {
    }
}
