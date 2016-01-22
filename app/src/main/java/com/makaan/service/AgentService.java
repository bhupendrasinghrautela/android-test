package com.makaan.service;

import com.makaan.event.agents.callback.TopAgentsCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;

import java.util.ArrayList;

import static com.makaan.constants.ApiConstants.TOP_AGENTS;
import static com.makaan.constants.ApiConstants.TOP_AGENTS_CITY;
import static com.makaan.constants.RequestConstants.BEDROOMS;
import static com.makaan.constants.RequestConstants.LISTING_CATEGORY;
import static com.makaan.constants.RequestConstants.LOCALITY_ID;
import static com.makaan.constants.RequestConstants.PRIMARY;
import static com.makaan.constants.RequestConstants.RENTAL;
import static com.makaan.constants.RequestConstants.RESALE;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 20/01/16.
 */
public class AgentService implements MakaanService{

    public void getTopAgentsForLocality(Long cityId, Long localityId, int noOfAgents, boolean isRental,TopAgentsCallback topAgentsCallback) {
        ArrayList<String> localityIds = new ArrayList<>();
        localityIds.add(localityId.toString());
        getTopAgentsForLocality(cityId, localityIds, null, null, noOfAgents, isRental, topAgentsCallback);
    }


    public void getTopAgentsForLocality(Long cityId, ArrayList<String> localityIdList, ArrayList<String> bedrooms, String unitType, Integer noOfAgents, boolean isRental, TopAgentsCallback topAgentsCallback) {

        if (null != cityId) {
            StringBuilder topAgentsUrl = new StringBuilder(TOP_AGENTS_CITY);
            topAgentsUrl.append(cityId).append(TOP_AGENTS);


            Selector topAgentsSelector = new Selector();
            if (null != localityIdList && localityIdList.size() > 0) {
                topAgentsSelector.term(LOCALITY_ID, localityIdList);
            }
            if (null != bedrooms) {
                topAgentsSelector.term(BEDROOMS, bedrooms);
            }
            if (isRental) {
                topAgentsSelector.term(LISTING_CATEGORY, RENTAL);
            } else {
                topAgentsSelector.term(LISTING_CATEGORY, new String[]{RESALE, PRIMARY});
            }

            if (null != unitType) {
                topAgentsSelector.term(UNIT_TYPE_ID, unitType);
            }
            if (null != noOfAgents) {
                topAgentsSelector.page(0, noOfAgents);
            }

            topAgentsUrl = topAgentsUrl.append("?").append(topAgentsSelector.build());
            MakaanNetworkClient.getInstance().get(topAgentsUrl.toString(), topAgentsCallback);
        }

    }


  /*  public void getTopAgentsByLocalityRequest(Selector topAgentsByLocalitySelector, String cityId) {
        topAgentsByLocalitySelector.range(PRICE, 0D, 50000000D);
        topAgentsByLocalitySelector.term(UNIT_TYPE, "apartment");
        topAgentsByLocalitySelector.term(LISTING_CATEGORY, "Primary").term(LISTING_CATEGORY, "Resale");
        topAgentsByLocalitySelector.term(BEDROOMS, "2").term(BEDROOMS, "3");
        topAgentsByLocalitySelector.term(LOCALITY_IDS, "50186");
        topAgentsByLocalitySelector.page(0, 20);

        if (null != topAgentsByLocalitySelector) {
            // String topAgentsByLocalityDetailsURL = ApiConstants.TOP_AGENTS.concat(cityId).concat(RequestConstants.TOP_AGENTS).concat(topAgentsByLocalitySelector.build());
            String topAgentsByLocalityDetailsURL = ApiConstants.TOP_AGENTS_CITY + (cityId) + (ApiConstants.TOP_AGENTS) + (topAgentsByLocalitySelector.build() + "&sourceDomain=Makaan");
            Log.e("selector Top seller", topAgentsByLocalityDetailsURL);
            MakaanNetworkClient.getInstance().get(topAgentsByLocalityDetailsURL, new TopAgentsGetCallBack());
        }
    }*/


}
