package com.makaan.service.listing;

import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.ApiListingDetail;
import com.makaan.response.serp.event.ListingGetCallback;
import com.makaan.service.MakaanService;

import org.json.JSONObject;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void getListingDetail(Long listingId) {

        MakaanNetworkClient.getInstance().get("https://marketplace-qa.proptiger-ws.com/app/v1/listing?selector={\"filters\":{\"and\":[{\"equal\":{\"cityId\":\"2\"}}]},\"paging\":{\"start\":0,\"rows\":10}}",
                new ListingGetCallback());

    }

}
