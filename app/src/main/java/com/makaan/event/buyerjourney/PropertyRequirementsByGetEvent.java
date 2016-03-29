package com.makaan.event.buyerjourney;

import com.makaan.event.MakaanEvent;
import com.makaan.response.buyerjourney.ClientLead;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 3/23/16.
 */
public class PropertyRequirementsByGetEvent extends MakaanEvent {
    public int totalCount;
    public ArrayList<ClientLead.PropertyRequirement> results;
}
