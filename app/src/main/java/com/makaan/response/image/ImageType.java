package com.makaan.response.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ImageType implements Parcelable {

    public Long id;
    public String type, displayName;
    public Integer objectTypeId, mediaTypeId;
    public ImageObjectType objectType;
    public ImageMediaType mediaType;

    protected ImageType(Parcel in) {
        type = in.readString();
        displayName = in.readString();
    }

    public static final Creator<ImageType> CREATOR = new Creator<ImageType>() {
        @Override
        public ImageType createFromParcel(Parcel in) {
            return new ImageType(in);
        }

        @Override
        public ImageType[] newArray(int size) {
            return new ImageType[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(displayName);
    }
}
