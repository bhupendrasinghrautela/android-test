package com.makaan.response.serp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by vaibhav on 04/01/16.
 */
public class FilterGroup implements Parcelable, FinderFilterable, Cloneable {

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
    public int layoutType;
    public boolean isSelected = false;

    public ArrayList<TermFilter> termFilterValues = new ArrayList<>();
    public ArrayList<RangeFilter> rangeFilterValues = new ArrayList<>();
    public ArrayList<RangeMinMaxFilter> rangeMinMaxFilterValues = new ArrayList<>();


    public FilterGroup(Parcel in) {

        this.displayName = in.readString();
        this.internalName = in.readString();
        this.displayOrder = in.readInt();
        this.layoutType = in.readInt();

        this.termFilterValues = new ArrayList<>();
        in.readTypedList(termFilterValues, TermFilter.CREATOR);

        this.rangeFilterValues = new ArrayList<>();
        in.readTypedList(rangeFilterValues, RangeFilter.CREATOR);

        this.rangeMinMaxFilterValues = new ArrayList<>();
        in.readTypedList(rangeMinMaxFilterValues, RangeFilter.CREATOR);

    }


    public FilterGroup() {
    }

    public FilterGroup(FilterGroup filterGroup) throws CloneNotSupportedException {
        this.displayName = filterGroup.displayName;
        this.internalName = filterGroup.internalName;
        this.displayOrder = filterGroup.displayOrder;
        this.layoutType = filterGroup.layoutType;

        for(TermFilter filter : filterGroup.termFilterValues) {
            this.termFilterValues.add(filter.clone());
        }

        for(RangeFilter filter : filterGroup.rangeFilterValues) {
            this.rangeFilterValues.add(filter.clone());
        }

        for(RangeMinMaxFilter filter : filterGroup.rangeMinMaxFilterValues) {
            this.rangeMinMaxFilterValues.add(filter.clone());
        }
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
        parcel.writeTypedList(rangeMinMaxFilterValues);
    }

    @Override
    public String getId() {
        return internalName;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    public void reset() {

        for (TermFilter termFilter : termFilterValues) {
            termFilter.selected = false;
        }
        for (RangeFilter rangeFilter : rangeFilterValues) {
            rangeFilter.selected = false;
        }

    }

    @Override
    public FilterGroup clone() throws CloneNotSupportedException {
        return new FilterGroup(this);
    }

    public void applyFilters(FilterGroup filterGroup) {
        this.termFilterValues = new ArrayList<>();
        this.rangeFilterValues = new ArrayList<>();

        this.termFilterValues.addAll(filterGroup.termFilterValues);
        this.rangeFilterValues.addAll(filterGroup.rangeFilterValues);
        this.rangeMinMaxFilterValues.addAll(filterGroup.rangeMinMaxFilterValues);
    }
}
