package com.makaan.response.trend;

import java.util.Date;

/**
 * Created by vaibhav on 11/01/16.
 */
public class PriceTrendData {

    public Date date;
    public Long minPricePerUnitArea;


    public PriceTrendData(Date date, Long minPricePerUnitArea) {
        this.date = date;
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public PriceTrendData() {
    }
}
