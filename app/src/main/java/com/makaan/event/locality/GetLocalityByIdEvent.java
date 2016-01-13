package com.makaan.event.locality;

import com.makaan.response.locality.Locality;

/**
 * Created by vaibhav on 09/01/16.
 */
public class GetLocalityByIdEvent {

    public Locality locality;

    public GetLocalityByIdEvent(Locality locality) {
        this.locality = locality;
    }

    public GetLocalityByIdEvent() {
    }
}
