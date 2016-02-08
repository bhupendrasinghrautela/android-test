package com.makaan.event.project;

import com.makaan.event.MakaanEvent;
import com.makaan.response.project.Project;

import java.util.ArrayList;

/**
 * Created by vaibhav on 23/01/16.
 */
public class SimilarProjectGetEvent extends MakaanEvent{

    public Long parentProjectId;
    public ArrayList<Project> similarProjects;

    public SimilarProjectGetEvent(Long parentProjectId, ArrayList<Project> similarProjects) {
        this.parentProjectId = parentProjectId;
        this.similarProjects = similarProjects;
    }

    public SimilarProjectGetEvent() {
    }
}
