package com.makaan.event.trend.callback;

import com.makaan.constants.MessageConstants;
import com.makaan.event.trend.TopLocalitiesTrendEvent;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.util.AppBus;




/**
 * Created by vaibhav on 11/01/16.
 */
public class TopLocalitiesTrendCallback extends LocalityTrendCallback {
    public static final String TAG = TopLocalitiesTrendCallback.class.getSimpleName();

    @Override
    public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
        TopLocalitiesTrendEvent topLocalitiesTrendEvent = new TopLocalitiesTrendEvent();
        if (null == localityPriceTrendDto || localityPriceTrendDto.data.size() == 0) {
            topLocalitiesTrendEvent.message = MessageConstants.LOCALITY_PRICE_TREND_NOT_AVAIL;
            AppBus.getInstance().post(topLocalitiesTrendEvent);
        } else {
            topLocalitiesTrendEvent.localityPriceTrendDto = localityPriceTrendDto;
            AppBus.getInstance().post(topLocalitiesTrendEvent);
        }
    }

}
