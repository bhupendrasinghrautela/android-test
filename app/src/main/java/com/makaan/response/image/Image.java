package com.makaan.response.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vaibhav on 17/01/16.
 */
public class Image implements Parcelable {

    public Long id;
    public Long imageTypeId,objectId, createdAt;
    public int statusId;
    public String altText, activeStatus;
    public String title, absolutePath;
    public ImageType imageType;
    public double width,height;

    protected Image(Parcel in) {
        statusId = in.readInt();
        altText = in.readString();
        activeStatus = in.readString();
        title = in.readString();
        absolutePath = in.readString();
        width = in.readDouble();
        height = in.readDouble();
        imageType = in.readParcelable(ImageType.class.getClassLoader());
    }

    public Image(){}
    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusId);
        dest.writeString(altText);
        dest.writeString(activeStatus);
        dest.writeString(title);
        dest.writeString(absolutePath);
        dest.writeDouble(width);
        dest.writeDouble(height);
        dest.writeParcelable(imageType, flags);
    }
}
