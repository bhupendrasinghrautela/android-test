package com.makaan.response.serp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 04/01/16.
 */
public class RangeFilter extends AbstractFilterValue {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RangeFilter createFromParcel(Parcel in) {
            return new RangeFilter(in);
        }

        public RangeFilter[] newArray(int size) {
            return new RangeFilter[size];
        }
    };

    public double minValue;
    public double maxValue;

    public double selectedMinValue;
    public double selectedMaxValue;

    public RangeFilter(Parcel in) {
        super(in);
        this.minValue = in.readDouble();
        this.maxValue = in.readDouble();

    }

    public RangeFilter() {

    }

    public RangeFilter(RangeFilter rangeFilter) {
        super(rangeFilter);
        this.minValue = rangeFilter.minValue;
        this.maxValue = rangeFilter.maxValue;
        this.selectedMinValue = rangeFilter.selectedMinValue;
        this.selectedMaxValue = rangeFilter.selectedMaxValue;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeDouble(minValue);
        parcel.writeDouble(maxValue);

    }

    @Override
    protected RangeFilter clone() throws CloneNotSupportedException {
        return new RangeFilter(this);
    }
}
