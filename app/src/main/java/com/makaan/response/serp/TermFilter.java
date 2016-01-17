package com.makaan.response.serp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 31/12/15.
 */
public class TermFilter extends AbstractFilterValue {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TermFilter createFromParcel(Parcel in) {
            return new TermFilter(in);
        }

        public TermFilter[] newArray(int size) {
            return new TermFilter[size];
        }
    };


    public String value;


    public TermFilter() {

    }

    public TermFilter(Parcel in) {
        super(in);
        this.value = in.readString();

    }

    public TermFilter(TermFilter termFilter) {
        super(termFilter);
        this.value = termFilter.value;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(value);

    }

    @Override
    protected TermFilter clone() throws CloneNotSupportedException {
        return new TermFilter(this);
    }
}
