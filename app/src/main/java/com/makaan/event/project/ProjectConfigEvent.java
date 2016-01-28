package com.makaan.event.project;

import android.os.Parcel;
import android.os.Parcelable;

import com.makaan.event.MakaanEvent;
import com.makaan.pojo.ProjectConfigItem;

import java.util.ArrayList;

/**
 * Created by vaibhav on 23/01/16.
 */
public class ProjectConfigEvent extends MakaanEvent implements Parcelable {


    public ArrayList<ProjectConfigItem> buyProjectConfigItems = new ArrayList<>();
    public ArrayList<ProjectConfigItem> rentProjectConfigItems = new ArrayList<>();

    public ProjectConfigEvent(ArrayList<ProjectConfigItem> buyProjectConfigItems, ArrayList<ProjectConfigItem> rentProjectConfigItems) {
        this.buyProjectConfigItems = buyProjectConfigItems;
        this.rentProjectConfigItems = rentProjectConfigItems;
    }

    public ProjectConfigEvent() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.buyProjectConfigItems);
        dest.writeList(this.rentProjectConfigItems);
    }

    protected ProjectConfigEvent(Parcel in) {
        this.buyProjectConfigItems = new ArrayList<ProjectConfigItem>();
        in.readList(this.buyProjectConfigItems, ArrayList.class.getClassLoader());
        this.rentProjectConfigItems = new ArrayList<ProjectConfigItem>();
        in.readList(this.rentProjectConfigItems, ArrayList.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProjectConfigEvent> CREATOR = new Parcelable.Creator<ProjectConfigEvent>() {
        public ProjectConfigEvent createFromParcel(Parcel source) {
            return new ProjectConfigEvent(source);
        }

        public ProjectConfigEvent[] newArray(int size) {
            return new ProjectConfigEvent[size];
        }
    };
}
