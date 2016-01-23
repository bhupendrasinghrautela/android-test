package com.makaan.event.city;

import com.makaan.event.MakaanEvent;
import com.makaan.response.city.CityTrendData;

import java.util.ArrayList;

/**
 * Created by vaibhav on 22/01/16.
 */
public class CityTrendEvent extends MakaanEvent{

    public ArrayList<CityTrendData> cityTrendData;

    public CityTrendEvent(ArrayList<CityTrendData> cityTrendData) {
        this.cityTrendData = cityTrendData;
    }

    public CityTrendEvent() {
    }
}
