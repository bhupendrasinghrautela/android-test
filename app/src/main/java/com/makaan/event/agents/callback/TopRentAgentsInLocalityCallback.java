package com.makaan.event.agents.callback;

import com.makaan.event.agents.TopRentAgentsInLocalityEvent;
import com.makaan.response.agents.TopAgent;
import com.makaan.util.AppBus;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopRentAgentsInLocalityCallback extends TopAgentsCallback {


    @Override
    public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {
        AppBus.getInstance().post(new TopRentAgentsInLocalityEvent(topAgents));
    }
}
