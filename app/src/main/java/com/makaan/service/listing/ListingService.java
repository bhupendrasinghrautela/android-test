package com.makaan.service.listing;

import com.makaan.constants.ApiConstants;
import com.makaan.event.listing.ListingByIdCallback;
import com.makaan.event.serp.SerpGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import com.makaan.service.MakaanService;

import java.util.ArrayList;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void handleSerpRequest(SerpRequest serpRequest) {
        /**
         *
         * build selector make actual network call and use SerpGetCallback
         */

        Selector selector = new Selector();

        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("2");
        selector.term("cityId", "2").range("price", 10000D, 5000000D).page(0, 20).sort("price","DESC");

        String selectorStr  = selector.build();

        System.out.println(selector.build());



        MakaanNetworkClient.getInstance().get("https://marketplace-qa.proptiger-ws.com/app/v1/listing?".concat(selectorStr),
                new SerpGetCallback(),null);
    }


    public void getListingDetail(Long listingId) {

        if (null != listingId) {
            String listingDetailURL = ApiConstants.LISTING.concat(listingId.toString());
            MakaanNetworkClient.getInstance().get(listingDetailURL, new ListingByIdCallback(), null);
        }

    }

}
