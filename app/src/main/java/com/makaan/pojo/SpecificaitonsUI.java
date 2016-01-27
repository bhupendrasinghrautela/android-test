package com.makaan.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 23/01/16.
 */
public class SpecificaitonsUI implements Parcelable{

    public String label1;
    public String label2;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label1);
        dest.writeString(label2);
    }
    private SpecificaitonsUI(Parcel in) {
        label1 = in.readString();
        label2 = in.readString();
    }

    public SpecificaitonsUI() {}

    public static final Parcelable.Creator<SpecificaitonsUI> CREATOR
            = new Parcelable.Creator<SpecificaitonsUI>() {

        @Override
        public SpecificaitonsUI createFromParcel(Parcel in) {
            return new SpecificaitonsUI(in);
        }

        @Override
        public SpecificaitonsUI[] newArray(int size) {
            return new SpecificaitonsUI[size];
        }
    };
}
