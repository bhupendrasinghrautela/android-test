package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.builder.BuilderByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.project.Builder;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by vaibhav on 22/01/16.
 */
public class BuilderService implements  MakaanService {


    /**
     * http://mp-qa1.makaan-ws.com/app/v1/builder-detail/100002?selector={%22fields%22:[%22id%22,%22name%22,%22description%22,%22imageURL%22,%22projectStatusCount%22,%22establishedDate%22,%22percentageCompletionOnTime%22]}&sourceDomain=Makaan
     */
    public void getBuilderById(Long builderId) {

        if (null != builderId) {
            Selector builderSelector = new Selector();
            builderSelector.fields(new String[]{"id", "name", "description", "imageURL", "projectStatusCount", "establishedDate", "percentageCompletionOnTime"});


            String builderUrl = ApiConstants.BUILDER_DETAIL.concat("/").concat(builderId.toString()).concat("?").concat(builderSelector.build());

            Type builderType = new TypeToken<Builder>() {}.getType();

            MakaanNetworkClient.getInstance().get(builderUrl, builderType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    BuilderByIdEvent builderByIdEvent = new BuilderByIdEvent();
                    builderByIdEvent.error = error;
                    AppBus.getInstance().post(builderByIdEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    Builder builder = (Builder) responseObject;

                    AppBus.getInstance().post(new BuilderByIdEvent(builder));
                }
            });
        }


    }
}
