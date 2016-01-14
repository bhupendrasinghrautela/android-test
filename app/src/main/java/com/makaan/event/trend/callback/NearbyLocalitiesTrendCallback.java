package com.makaan.event.trend.callback;

import com.makaan.constants.MessageConstants;
import com.makaan.event.trend.NearByLocalitiesTrendEvent;
import com.makaan.event.trend.TopLocalitiesTrendEvent;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.util.AppBus;

/**
 * Created by vaibhav on 11/01/16.
 *
 */
public class NearbyLocalitiesTrendCallback extends LocalityTrendCallback {

    public static final String TAG = TopLocalitiesTrendCallback.class.getSimpleName();

    @Override
    public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
        NearByLocalitiesTrendEvent nearByLocalitiesTrendEvent = new NearByLocalitiesTrendEvent();
        if (null == localityPriceTrendDto || localityPriceTrendDto.data.size() == 0) {
            nearByLocalitiesTrendEvent.message = MessageConstants.LOCALITY_PRICE_TREND_NOT_AVAIL;
            AppBus.getInstance().post(nearByLocalitiesTrendEvent);
        } else {
            nearByLocalitiesTrendEvent.localityPriceTrendDto = localityPriceTrendDto;
            AppBus.getInstance().post(nearByLocalitiesTrendEvent);
        }
    }
}
