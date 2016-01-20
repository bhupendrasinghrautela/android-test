package com.makaan.event.locality;

import com.makaan.event.MakaanEvent;
import com.makaan.response.project.Builder;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopBuilderInLocalityEvent extends MakaanEvent {

    public ArrayList<Builder> builders;

    public TopBuilderInLocalityEvent(ArrayList<Builder> builders) {
        this.builders = builders;
    }

    public TopBuilderInLocalityEvent() {
    }
}
