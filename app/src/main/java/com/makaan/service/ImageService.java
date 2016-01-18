package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.listing.ListingImagesGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.listing.detail.ListingDetailImage;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ImageService implements MakaanService {


    public void getListingImages(final Long listingId) {

        if (null != listingId) {
            Type listingDetailImageList = new TypeToken<ArrayList<ListingDetailImage>>() {
            }.getType();
            StringBuilder detailImageListUrl = new StringBuilder(ApiConstants.LISTING_IMAGE);
            detailImageListUrl.append(listingId.toString());

            MakaanNetworkClient.getInstance().get(detailImageListUrl.toString(), listingDetailImageList, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    ArrayList<ListingDetailImage> detailImages = (ArrayList<ListingDetailImage>) responseObject;
                    ListingImagesGetEvent listingImagesGetEvent = new ListingImagesGetEvent();
                    listingImagesGetEvent.listingId = listingId;
                    listingImagesGetEvent.listingDetailImages = detailImages;
                    AppBus.getInstance().post(listingImagesGetEvent);
                }
            }, true);
        }

    }
}
