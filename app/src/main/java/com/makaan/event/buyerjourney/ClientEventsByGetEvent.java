package com.makaan.event.buyerjourney;

import com.makaan.event.MakaanEvent;
import com.makaan.response.buyerjourney.ClientEvent;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientEventsByGetEvent extends MakaanEvent {
    public int totalCount;
    public ArrayList<ClientEvent> results;
}
