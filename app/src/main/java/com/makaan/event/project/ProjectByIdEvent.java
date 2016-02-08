package com.makaan.event.project;

import com.makaan.event.MakaanEvent;
import com.makaan.response.project.Project;

/**
 * Created by vaibhav on 23/01/16.
 */
public class ProjectByIdEvent extends MakaanEvent{

    public Project project;

    public ProjectByIdEvent(Project project) {
        this.project = project;
    }

    public ProjectByIdEvent() {
    }
}
