package com.makaan.service;

import com.makaan.constants.ApiConstants;
import com.makaan.event.listing.ListingByIdCallback;
import com.makaan.event.serp.BaseSerpCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.request.selector.Selector;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void handleSerpRequest(Selector serpSelector) {

        /*Selector selector = new Selector();

        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("2");
        selector.term("cityId", "2").range("price", 10000D, 5000000D).page(0, 20).sort("price", "SORT_DESC");

        String selectorStr = selector.build();

        System.out.println(selector.build());*/

        if(null != serpSelector){
            String serpDetailsURL = ApiConstants.LISTING.concat("?").concat(serpSelector.build());

            MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        }


    }


    public void getListingDetail(Long listingId) {

        if (null != listingId) {

            Selector listingDetailSelector = new Selector();
            listingDetailSelector.fields(new String[]{NEIGHBOURHOOD_AMENITIES_IDS, FURNISHINGS});
            String listingDetailURL = ApiConstants.LISTING.concat(listingId.toString());

            MakaanNetworkClient.getInstance().get(listingDetailURL, new ListingByIdCallback());
        }

    }

}
