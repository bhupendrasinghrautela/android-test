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

    public RangeFilter(Parcel in) {
        super(in);
        this.minValue = in.readDouble();
        this.maxValue = in.readDouble();

    }

    public RangeFilter() {

    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeDouble(minValue);
        parcel.writeDouble(maxValue);

    }
}
