package com.makaan.service.pyr;

import android.util.Log;

import static com.makaan.constants.RequestConstants.*;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.RequestConstants;
import com.makaan.event.pyr.TopAgentsGetCallBack;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.service.MakaanService;

/**
 * Created by makaanuser on 9/1/16.
 */
public class TopAgentsByLocalityService implements MakaanService {

    public void getTopAgentsByLocalityRequest(Selector topAgentsByLocalitySelector,String cityId) {
        topAgentsByLocalitySelector.range(PRICE, 0D, 50000000D);
        topAgentsByLocalitySelector.term(UNIT_TYPE, "apartment");
        topAgentsByLocalitySelector.term(LISTING_CATEGORY, "Primary").term(LISTING_CATEGORY,"Resale");
        topAgentsByLocalitySelector.term(BEDROOMS,"2").term(BEDROOMS, "3");
        topAgentsByLocalitySelector.term(LOCALITY_IDS, "50186");
        topAgentsByLocalitySelector.page(0,20);

        if(null != topAgentsByLocalitySelector){
           // String topAgentsByLocalityDetailsURL = ApiConstants.TOP_AGENTS.concat(cityId).concat(RequestConstants.TOP_AGENTS).concat(topAgentsByLocalitySelector.build());
            String topAgentsByLocalityDetailsURL = ApiConstants.TOP_AGENTS_URL+(cityId)+(ApiConstants.TOP_AGENTS)+(topAgentsByLocalitySelector.build()+"&sourceDomain=Makaan");
            Log.e("selector Top seller",topAgentsByLocalityDetailsURL);
            MakaanNetworkClient.getInstance().get(topAgentsByLocalityDetailsURL, new TopAgentsGetCallBack());
        }
    }
}
