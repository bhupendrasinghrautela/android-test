package com.makaan.event.buyerjourney;

import com.makaan.event.MakaanEvent;
import com.makaan.response.buyerjourney.ClientLead;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientLeadsByGetEvent extends MakaanEvent {
    public ArrayList<ClientLead> results;
}
