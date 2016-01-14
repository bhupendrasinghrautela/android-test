package com.makaan.event.locality;

import com.makaan.response.locality.Locality;

/**
 * Created by vaibhav on 09/01/16.
 */
public class LocalityByIdEvent {

    public Locality locality;

    public LocalityByIdEvent(Locality locality) {
        this.locality = locality;
    }

    public LocalityByIdEvent() {
    }
}
