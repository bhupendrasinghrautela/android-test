package com.makaan.event.trend;

import com.makaan.event.MakaanEvent;
import com.makaan.response.trend.LocalityPriceTrendDto;

/**
 * Created by vaibhav on 12/01/16.
 */
public class CityTrendEvent extends MakaanEvent{

    public LocalityPriceTrendDto localityPriceTrendDto;

    public CityTrendEvent(LocalityPriceTrendDto localityPriceTrendDto) {
        this.localityPriceTrendDto = localityPriceTrendDto;
    }
}
