package com.makaan.response.serp;

import android.os.Parcel;

/**
 * Created by vaibhav on 04/01/16.
 */
public class RangeMinMaxFilter extends AbstractFilterValue {

    public static final Creator CREATOR = new Creator() {
        public RangeMinMaxFilter createFromParcel(Parcel in) {
            return new RangeMinMaxFilter(in);
        }

        public RangeMinMaxFilter[] newArray(int size) {
            return new RangeMinMaxFilter[size];
        }
    };

    public double minValue;
    public double maxValue;

    public String minFieldName;
    public String maxFieldName;

    public RangeMinMaxFilter(Parcel in) {
        super(in);
        this.minValue = in.readDouble();
        this.maxValue = in.readDouble();
        this.minFieldName = in.readString();
        this.maxFieldName = in.readString();

    }

    public RangeMinMaxFilter() {

    }

    public RangeMinMaxFilter(RangeMinMaxFilter minMaxRangeFilter) {
        super(minMaxRangeFilter);
        this.minValue = minMaxRangeFilter.minValue;
        this.maxValue = minMaxRangeFilter.maxValue;
        this.minFieldName = minMaxRangeFilter.minFieldName;
        this.maxFieldName = minMaxRangeFilter.maxFieldName;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeDouble(minValue);
        parcel.writeDouble(maxValue);
        parcel.writeString(minFieldName);
        parcel.writeString(maxFieldName);

    }

    @Override
    protected RangeMinMaxFilter clone() throws CloneNotSupportedException {
        return new RangeMinMaxFilter(this);
    }
}
