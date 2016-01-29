package com.makaan.event.project;

import com.makaan.pojo.ProjectConfigItem;
import com.makaan.ui.project.ProjectConfigItemView;

/**
 * Created by tusharchaudhary on 1/29/16.
 */
public class ProjectConfigItemClickListener {
    public ProjectConfigItem projectConfigItem;
    public ProjectConfigItemView.ConfigItemType configItemType;
    public boolean isRent;

    public ProjectConfigItemClickListener(ProjectConfigItem projectConfigItem, ProjectConfigItemView.ConfigItemType configItemType,boolean isRent) {
        this.projectConfigItem = projectConfigItem;
        this.configItemType = configItemType;
        this.isRent = isRent;
    }
}
