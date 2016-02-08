package com.makaan.event.location;

import com.makaan.response.BaseEvent;
import com.makaan.response.location.MyLocation;

/**
 * Created by rohitgarg on 2/8/16.
 */
public class LocationGetEvent extends BaseEvent {

    public MyLocation myLocation;

    public LocationGetEvent(MyLocation myLocation) {
        this.myLocation = myLocation;
    }

    public LocationGetEvent() {
    }
}
