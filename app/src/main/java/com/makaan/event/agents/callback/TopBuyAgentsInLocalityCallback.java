package com.makaan.event.agents.callback;

import com.makaan.event.agents.TopBuyAgentsInLocalityEvent;
import com.makaan.response.agents.TopAgent;
import com.makaan.util.AppBus;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopBuyAgentsInLocalityCallback  extends TopAgentsCallback{

    @Override
    public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {

        AppBus.getInstance().post(new TopBuyAgentsInLocalityEvent(topAgents));
    }
}
