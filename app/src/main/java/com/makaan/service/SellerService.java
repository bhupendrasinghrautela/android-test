package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.seller.SellerByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.project.CompanySeller;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by vaibhav on 22/01/16.
 */
public class SellerService implements  MakaanService {


    /**
     * http://mp-qa1.makaan-ws.com/app/v1/builder-detail/100002?selector={%22fields%22:[%22id%22,%22name%22,%22description%22,%22imageURL%22,%22projectStatusCount%22,%22establishedDate%22,%22percentageCompletionOnTime%22]}&sourceDomain=Makaan
     */
    public void getSellerById(Long sellerId) {

        if (null != sellerId) {
            Selector builderSelector = new Selector();
            builderSelector.fields(new String[]{"id", "name", /*"address", */"score", "logo", "activeSince",
                    "coverPicture", "sellers", "sellerType", "cities", "label", "companyUser", "user", "fullName", "listingCount", "sellerListingData",
                    "localityCount", "projectCount", "categoryWiseCount", "listingCategoryType"});


            String sellerUrl = ApiConstants.SELLER_DETAIL.concat("/").concat(sellerId.toString()).concat("?").concat(builderSelector.build());

            Type sellerType = new TypeToken<CompanySeller>() {}.getType();

            MakaanNetworkClient.getInstance().get(sellerUrl, sellerType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    SellerByIdEvent sellerByIdEvent = new SellerByIdEvent();
                    sellerByIdEvent.error = error;
                    AppBus.getInstance().post(sellerByIdEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    CompanySeller seller = (CompanySeller) responseObject;

                    AppBus.getInstance().post(new SellerByIdEvent(seller));
                }
            });
        }


    }
}
