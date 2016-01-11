package com.makaan.event.city;

import com.makaan.response.city.City;

/**
 * Created by vaibhav on 09/01/16.
 */
public class GetCityByIdEvent {

    public City city;

    public GetCityByIdEvent(City city) {
        this.city = city;
    }

    public GetCityByIdEvent() {
    }
}
