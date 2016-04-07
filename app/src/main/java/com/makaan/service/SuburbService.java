package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.suburb.SuburbByIdGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.locality.Suburb;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by rohitgarg on 4/6/16.
 */
public class SuburbService implements MakaanService {
    public void getSuburbByIdForEnquiry(Long suburbId) {

        if (null != suburbId) {

            String suburbUrl = ApiConstants.SUBURB.concat(suburbId.toString());

            Selector suburbSelector = new Selector();

            suburbSelector.fields(new String[]{"suburbId", "id", "label", "city", "name"});

            suburbUrl = suburbUrl.concat("?").concat(suburbSelector.build());
            Type suburbType = new TypeToken<Suburb>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(suburbUrl, suburbType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    SuburbByIdGetEvent suburbByIdGetEvent = new SuburbByIdGetEvent();
                    suburbByIdGetEvent.error = error;
                    AppBus.getInstance().post(suburbByIdGetEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    Suburb suburb = (Suburb) responseObject;
                    AppBus.getInstance().post(new SuburbByIdGetEvent(suburb));
                }
            });
        }

    }
}
