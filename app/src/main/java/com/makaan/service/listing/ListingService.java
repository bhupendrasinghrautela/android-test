package com.makaan.service.listing;

import com.makaan.constants.ApiConstants;
import com.makaan.event.listing.ListingByIdCallback;
import com.makaan.event.serp.SerpGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.SerpRequest;
import com.makaan.service.MakaanService;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void handleSerpRequest(SerpRequest serpRequest) {
        /**
         *
         * build selector make actual network call and use SerpGetCallback
         */

        MakaanNetworkClient.getInstance().get("https://marketplace-qa.proptiger-ws.com/app/v1/listing?selector={\"filters\":{\"and\":[{\"equal\":{\"cityId\":\"2\"}}]},\"paging\":{\"start\":0,\"rows\":10}}",
                new SerpGetCallback());
    }


    public void getListingDetail(Long listingId) {

        if (null != listingId) {
            String listingDetailURL = ApiConstants.LISTING.concat(listingId.toString());
            MakaanNetworkClient.getInstance().get(listingDetailURL, new ListingByIdCallback());
        }

    }

}
