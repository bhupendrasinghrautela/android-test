package com.makaan.event.trend;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.MessageConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.trend.ApiPriceTrend;
import com.makaan.response.trend.ApiPriceTrendData;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.makaan.constants.ResponseConstants.*;

/**
 * Created by vaibhav on 11/01/16.
 */
public class CityTrendChartCallback extends JSONGetCallback {
    public static final String TAG = CityTrendChartCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {

        LocalityPriceTrendDto localityPriceTrendDto = new LocalityPriceTrendDto();
        CityTrendEvent cityTrendEvent = new CityTrendEvent(localityPriceTrendDto);


        Type apiPriceTrendType = new TypeToken<ArrayList<ApiPriceTrend>>() {
        }.getType();
        ApiPriceTrend apiPriceTrend = MakaanBuyerApplication.gson.fromJson(dataResponse.toString(), apiPriceTrendType);


        if (null != apiPriceTrend && null != apiPriceTrend.data) {
            for (Map.Entry<Long, HashMap<String, List<ApiPriceTrendData>>> entry : apiPriceTrend.data.entrySet()) {
                Long localityId = entry.getKey();
                HashMap<String, List<ApiPriceTrendData>> localityTrendData = entry.getValue();

                if (null != localityTrendData) {
                    for (Map.Entry<String, List<ApiPriceTrendData>> trendEntry : localityTrendData.entrySet()) {
                        String epochDataDate = trendEntry.getKey();
                        Date dataDate = AppUtils.getDateFromEpoch(epochDataDate);

                        List<ApiPriceTrendData> apiPriceTrendList = trendEntry.getValue();
                        if (null != apiPriceTrendList && apiPriceTrendList.size() > 0) {
                            ApiPriceTrendData apiPriceTrendData = apiPriceTrendList.get(0);
                            Long minPricePerUnitArea = apiPriceTrendData.extraAttributes.get(MIN_PRICE_PER_UNIT_AREA);
                            if (null != minPricePerUnitArea) {
                                localityPriceTrendDto.addPriceTrendData(localityId, apiPriceTrendData.localityName, dataDate, minPricePerUnitArea);
                            }
                        }

                    }

                }
                AppBus.getInstance().post(cityTrendEvent);
            }
        } else {
            cityTrendEvent.message = MessageConstants.LOCALITY_PRICE_TREND_NOT_AVAIL;
            AppBus.getInstance().post(cityTrendEvent);
        }


    }
}
