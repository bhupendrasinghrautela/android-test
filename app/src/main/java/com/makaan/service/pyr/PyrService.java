package com.makaan.service.pyr;

import com.makaan.constants.ApiConstants;
import com.makaan.event.pyr.TopAgentLocalityGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 09/01/16.
 */
public class PyrService {


    public void getTopAgentsInLocality(Integer agentCount, Selector localityTopAgentSelector ){
        //localityTopAgentSelector.range(PRICE, 100, 500).term(UNIT_TYPE, "apartment").page(0, 5).build(); // "selector={json}"

        String topAgentInLocalityUrl = ApiConstants.TOP_AGENTS_LOCALITY.concat("/").concat(agentCount.toString()).concat("/top-agents?").concat(localityTopAgentSelector.build());
        MakaanNetworkClient.getInstance().get(topAgentInLocalityUrl, new TopAgentLocalityGetCallback());

    }
}
