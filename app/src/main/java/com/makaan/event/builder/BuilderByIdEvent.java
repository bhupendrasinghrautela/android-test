package com.makaan.event.builder;

import com.makaan.event.MakaanEvent;
import com.makaan.response.project.Builder;

/**
 * Created by vaibhav on 22/01/16.
 */
public class BuilderByIdEvent extends MakaanEvent{

    public Builder builder;

    public BuilderByIdEvent(Builder builder) {
        this.builder = builder;
    }

    public BuilderByIdEvent() {
    }
}
