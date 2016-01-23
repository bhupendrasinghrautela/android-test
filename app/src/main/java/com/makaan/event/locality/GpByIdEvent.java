package com.makaan.event.locality;

import com.makaan.response.locality.GpDetail;

/**
 * Created by vaibhav on 22/01/16.
 */
public class GpByIdEvent {

    public GpDetail gpDetail;

    public GpByIdEvent(GpDetail gpDetail) {
        this.gpDetail = gpDetail;
    }

    public GpByIdEvent() {
    }
}
