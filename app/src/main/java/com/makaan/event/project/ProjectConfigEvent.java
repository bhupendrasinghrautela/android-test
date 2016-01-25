package com.makaan.event.project;

import com.makaan.event.MakaanEvent;
import com.makaan.pojo.ProjectConfigItem;

import java.util.ArrayList;

/**
 * Created by vaibhav on 23/01/16.
 */
public class ProjectConfigEvent extends MakaanEvent{


    public ArrayList<ProjectConfigItem> buyProjectConfigItems = new ArrayList<>();
    public ArrayList<ProjectConfigItem> rentProjectConfigItems = new ArrayList<>();

    public ProjectConfigEvent(ArrayList<ProjectConfigItem> buyProjectConfigItems, ArrayList<ProjectConfigItem> rentProjectConfigItems) {
        this.buyProjectConfigItems = buyProjectConfigItems;
        this.rentProjectConfigItems = rentProjectConfigItems;
    }

    public ProjectConfigEvent() {
    }
}
