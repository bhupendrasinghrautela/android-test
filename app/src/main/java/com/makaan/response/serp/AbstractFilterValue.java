package com.makaan.response.serp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 31/12/15.
 *
 * represents base value for filter in serp
 */
public abstract class AbstractFilterValue  implements Parcelable {


    public String displayName;
    public int displayOrder;

    public String fieldName;            // this is the internal field name to be passed back to api


    public boolean selected;

    public AbstractFilterValue(Parcel in) {
        this.displayName = in.readString();
        this.displayOrder = in.readInt();
        this.fieldName = in.readString();
        this.selected = Boolean.parseBoolean(in.readString());

    }

    public AbstractFilterValue() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(displayName);
        parcel.writeInt(displayOrder);
        parcel.writeString(fieldName);

        parcel.writeString(Boolean.valueOf(selected).toString());

    }
}
