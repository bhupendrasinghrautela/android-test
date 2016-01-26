package com.makaan.response.locality;

import com.makaan.response.city.City;

/**
 * Created by vaibhav on 09/01/16.
 */
public class Suburb {

    public Long id;
    public Long cityId;

    public String label;

    public City city;

    @Override
    public String toString() {
        return "Suburb{" +
                "id=" + id +
                ", cityId=" + cityId +
                ", label='" + label + '\'' +
                ", city=" + city +
                '}';
    }
}
