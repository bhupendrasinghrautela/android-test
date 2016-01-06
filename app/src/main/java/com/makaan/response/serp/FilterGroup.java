package com.makaan.response.serp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by vaibhav on 04/01/16.
 */
public class FilterGroup implements Parcelable, FinderFilterable{

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FilterGroup createFromParcel(Parcel in) {
            return new FilterGroup(in);
        }

        public FilterGroup[] newArray(int size) {
            return new FilterGroup[size];
        }
    };


    public String displayName;
    public String internalName;
    public int displayOrder;

    public ArrayList<TermFilter> termFilterValues = new ArrayList<>();
    public ArrayList<RangeFilter> rangeFilterValues = new ArrayList<>();


    public FilterGroup(Parcel in) {

        this.displayName = in.readString();
        this.internalName = in.readString();
        this.displayOrder = in.readInt();

        this.termFilterValues = new ArrayList<>();
        in.readTypedList(termFilterValues, TermFilter.CREATOR);

        this.rangeFilterValues = new ArrayList<>();
        in.readTypedList(rangeFilterValues, RangeFilter.CREATOR);


    }

    public FilterGroup() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(displayName);
        parcel.writeString(internalName);
        parcel.writeTypedList(termFilterValues);
        parcel.writeTypedList(rangeFilterValues);
    }

    @Override
    public String getId() {
        return internalName;
    }

    @Override
    public String displayName() {
        return displayName;
    }





}
