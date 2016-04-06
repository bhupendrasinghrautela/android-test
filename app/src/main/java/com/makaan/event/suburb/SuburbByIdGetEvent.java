package com.makaan.event.suburb;

import com.makaan.event.MakaanEvent;
import com.makaan.response.locality.Suburb;

/**
 * Created by rohitgarg on 4/6/16.
 */
public class SuburbByIdGetEvent extends MakaanEvent {

    public Suburb suburb;

    public SuburbByIdGetEvent(Suburb suburb) {
        this.suburb = suburb;
    }

    public SuburbByIdGetEvent() {
    }
}
