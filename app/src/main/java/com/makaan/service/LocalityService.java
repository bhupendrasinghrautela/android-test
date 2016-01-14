package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.locality.Locality;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by vaibhav on 09/01/16.
 */
public class LocalityService implements MakaanService {

    /**
     *
     * https://marketplace-qa.proptiger-ws.com/app/v3/locality/50157
     */
    public void getLocalityById(Long localityId) {

        if (null != localityId) {

            String localityUrl = ApiConstants.LOCALITY.concat(localityId.toString());

            Type localityType = new TypeToken<Locality>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(localityUrl, localityType, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    Locality locality = (Locality) responseObject;
                    AppBus.getInstance().post(new LocalityByIdEvent(locality));
                }
            });
        }
    }

    public void getPriceTrendsInLocality(){

    }
}
