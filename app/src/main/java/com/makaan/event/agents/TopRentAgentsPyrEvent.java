package com.makaan.event.agents;

import com.makaan.event.MakaanEvent;
import com.makaan.response.agents.TopAgent;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopRentAgentsPyrEvent extends MakaanEvent {
    public ArrayList<TopAgent> topAgents = new ArrayList<>();

    public TopRentAgentsPyrEvent(ArrayList<TopAgent> topAgents) {
        this.topAgents = topAgents;
    }

    public TopRentAgentsPyrEvent() {
    }
}
