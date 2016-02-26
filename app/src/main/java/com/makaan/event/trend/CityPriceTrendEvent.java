package com.makaan.event.trend;

import com.makaan.event.MakaanEvent;
import com.makaan.response.trend.LocalityPriceTrendDto;

/**
 * Created by aishwarya on 25/02/16.
 */
public class CityPriceTrendEvent extends MakaanEvent {
    public LocalityPriceTrendDto cityPriceTrendDto;

    public CityPriceTrendEvent(LocalityPriceTrendDto projectPriceTrendDto) {
        this.cityPriceTrendDto = projectPriceTrendDto;
    }

    public CityPriceTrendEvent() {
    }
}
