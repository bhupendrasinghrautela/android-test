package com.makaan.response.trend;

/**
 * Created by vaibhav on 11/01/16.
 */
public class PriceTrendData implements Comparable<PriceTrendData> {

    public long date;
    public Long minPricePerUnitArea;


    public PriceTrendData(Long date, Long minPricePerUnitArea) {
        this.date = date;
        this.minPricePerUnitArea = minPricePerUnitArea;
    }

    public PriceTrendData() {
    }

    @Override
    public int compareTo(PriceTrendData another) {
        return this.date > another.date ? 1 : (this.date < another.date ? -1 : 0);
    }
}
