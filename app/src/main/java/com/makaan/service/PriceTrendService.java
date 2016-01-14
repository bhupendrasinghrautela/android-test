package com.makaan.service;

import com.makaan.constants.ApiConstants;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.event.trend.callback.TopLocalitiesTrendCallback;
import com.makaan.network.MakaanNetworkClient;

import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.FILTERS;
import static com.makaan.constants.RequestConstants.LOCALITY_ID;
import static com.makaan.constants.RequestConstants.MONTH_DURATION;

/**
 * Created by vaibhav on 13/01/16.
 */
public class PriceTrendService  implements MakaanService{

    /**
     * http://marketplace-qa.makaan-ws.com/data/v1/trend/hitherto?fields=minPricePerUnitArea,localityName,projectName&filters=localityId==51549,localityId==51751,localityId==53250,localityId==53133&monthDuration=6&group=localityId,month
     */

    public void getPriceTrendForLocalities(ArrayList<Long> topLocalityIds, int monthDuration, LocalityTrendCallback localityTrendCallback) {


        if (null != topLocalityIds && topLocalityIds.size() > 0 && monthDuration > 0) {
            int size = topLocalityIds.size();
            StringBuilder priceTrendUrl = new StringBuilder(ApiConstants.LOCALITY_TREND_URL);
            priceTrendUrl.append("&").append(MONTH_DURATION).append("=").append(monthDuration).append(FILTERS).append("=");

            for(int i=0 ; i < size; i++){
                Long localityId  = topLocalityIds.get(i);
                if(i != size -1){
                    priceTrendUrl.append(LOCALITY_ID).append("==").append(localityId).append(",");
                }else{
                    priceTrendUrl.append(LOCALITY_ID).append("==").append(localityId);
                }


            }

            MakaanNetworkClient.getInstance().get(priceTrendUrl.toString(), localityTrendCallback);
        }
    }
}
