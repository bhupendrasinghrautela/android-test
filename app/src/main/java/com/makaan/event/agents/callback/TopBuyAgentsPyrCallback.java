package com.makaan.event.agents.callback;

import com.makaan.event.agents.TopBuyAgentsPyrEvent;
import com.makaan.response.ResponseError;
import com.makaan.response.agents.TopAgent;
import com.makaan.util.AppBus;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/01/16.
 */
public class TopBuyAgentsPyrCallback extends TopAgentsCallback{


    @Override
    public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {
        AppBus.getInstance().post(new TopBuyAgentsPyrEvent(topAgents));
    }

    @Override
    public void onError(ResponseError error) {
        super.onError(error);
        TopBuyAgentsPyrEvent topBuyAgentsPyrEvent=new TopBuyAgentsPyrEvent();
        topBuyAgentsPyrEvent.error=error;
        AppBus.getInstance().post(topBuyAgentsPyrEvent);
    }
}
