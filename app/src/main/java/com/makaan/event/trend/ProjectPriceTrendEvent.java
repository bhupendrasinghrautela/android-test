package com.makaan.event.trend;

import com.makaan.event.MakaanEvent;
import com.makaan.response.trend.ProjectPriceTrendDto;

/**
 * Created by vaibhav on 27/01/16.
 */
public class ProjectPriceTrendEvent extends MakaanEvent {


    public ProjectPriceTrendDto projectPriceTrendDto;

    public ProjectPriceTrendEvent(ProjectPriceTrendDto projectPriceTrendDto) {
        this.projectPriceTrendDto = projectPriceTrendDto;
    }

    public ProjectPriceTrendEvent() {
    }
}
