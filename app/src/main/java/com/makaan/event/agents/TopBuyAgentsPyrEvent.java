package com.makaan.event.agents;

import com.makaan.event.MakaanEvent;
import com.makaan.response.agents.TopAgent;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopBuyAgentsPyrEvent extends MakaanEvent{
    public ArrayList<TopAgent> topAgents = new ArrayList<>();

    public TopBuyAgentsPyrEvent(ArrayList<TopAgent> topAgents) {
        this.topAgents = topAgents;
    }

    public TopBuyAgentsPyrEvent() {
    }
}
