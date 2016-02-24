package com.makaan.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by vaibhav on 24/01/16.
 */
public class ProjectConfigItem implements Parcelable {

    public double minPrice, maxPrice;
    public HashSet<Integer> bedrooms = new HashSet<>();
    public int propertyCount;
    public SellerCard topSellerCard;

    public HashMap<Long, SellerCard> companies = new HashMap<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.minPrice);
        dest.writeDouble(this.maxPrice);
        dest.writeSerializable(this.bedrooms);
        dest.writeInt(this.propertyCount);
        dest.writeSerializable(this.companies);
    }

    public ProjectConfigItem() {
    }

    protected ProjectConfigItem(Parcel in) {
        this.minPrice = in.readDouble();
        this.maxPrice = in.readDouble();
        this.bedrooms = (HashSet<Integer>) in.readSerializable();
        this.propertyCount = in.readInt();
        this.companies = (HashMap<Long, SellerCard>) in.readSerializable();
    }

    public static final Parcelable.Creator<ProjectConfigItem> CREATOR = new Parcelable.Creator<ProjectConfigItem>() {
        public ProjectConfigItem createFromParcel(Parcel source) {
            return new ProjectConfigItem(source);
        }

        public ProjectConfigItem[] newArray(int size) {
            return new ProjectConfigItem[size];
        }
    };
}
