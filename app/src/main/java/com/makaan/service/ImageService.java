package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ImageConstants;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.image.Image;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.ImageConstants.*;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ImageService implements MakaanService {


    public void getListingImages(final Long listingId) {
        getImages(listingId, ENTITY_MAP.get(ImageConstants.LISTING), BATHROOM);

    }


    public void getImages(final Long objectId, final Integer objectType, final String imageType) {
        if (null != objectId) {
            Type listingDetailImageList = new TypeToken<ArrayList<Image>>() {
            }.getType();
            String detailImageListUrl = ApiConstants.IMAGE;
            detailImageListUrl = detailImageListUrl.concat("?filters=(objectId==OBJ_ID;imageTypeObj.objectTypeId==OBJ_TYPE_ID;imageTypeObj.type==IMG_TYPE)&sourceDomain=Makaan");
            detailImageListUrl = detailImageListUrl.replaceAll("OBJ_ID", objectId.toString());
            detailImageListUrl = detailImageListUrl.replaceAll("OBJ_TYPE_ID", objectType.toString());
            detailImageListUrl = detailImageListUrl.replaceAll("IMG_TYPE", imageType);


            MakaanNetworkClient.getInstance().get(detailImageListUrl, listingDetailImageList, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    ArrayList<Image> images = (ArrayList<Image>) responseObject;
                    ImagesGetEvent imagesGetEvent = new ImagesGetEvent();
                    imagesGetEvent.objectId = objectId;
                    imagesGetEvent.objectType = objectType;
                    imagesGetEvent.imageType = imageType;

                    imagesGetEvent.images = images;
                    AppBus.getInstance().post(imagesGetEvent);
                }
            }, true);
        }
    }
}
