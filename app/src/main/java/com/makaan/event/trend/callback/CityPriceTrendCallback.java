package com.makaan.event.trend.callback;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.trend.CityPriceTrendEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.trend.ApiPriceTrendData;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.makaan.constants.ResponseConstants.AVG_BUY_PRICE_PER_UNIT_AREA;

/**
 * Created by aishwarya on 25/02/16.
 */
public class CityPriceTrendCallback extends JSONGetCallback {

    public static final String TAG = LocalityTrendCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {

        LocalityPriceTrendDto localityPriceTrendDto = new LocalityPriceTrendDto();

        HashMap<String, List<ApiPriceTrendData>> data ;
        Type apiPriceTrendType = new TypeToken<HashMap<String, List<ApiPriceTrendData>>>() {}.getType();

        try {
            data = MakaanBuyerApplication.gson.fromJson(dataResponse.getJSONObject(ResponseConstants.DATA).toString(), apiPriceTrendType);


            if (null != data) {
/*                for (Map.Entry<Long, HashMap<String, List<ApiPriceTrendData>>> entry : data.entrySet()) {
                    Long localityId = entry.getKey();
                    HashMap<String, List<ApiPriceTrendData>> localityTrendData = entry.getValue();

                    if (null != localityTrendData) {*/
                        for (Map.Entry<String, List<ApiPriceTrendData>> trendEntry : data.entrySet()) {
                            Long epochDataDate = Long.valueOf(trendEntry.getKey());
                            // Date dataDate = AppUtils.getDateFromEpoch(epochDataDate);

                            List<ApiPriceTrendData> apiPriceTrendList = trendEntry.getValue();
                            if (null != apiPriceTrendList && apiPriceTrendList.size() > 0) {
                                ApiPriceTrendData apiPriceTrendData = apiPriceTrendList.get(0);
                                Double minPricePerUnitArea = apiPriceTrendData.extraAttributes.get(AVG_BUY_PRICE_PER_UNIT_AREA);
                                if (null != minPricePerUnitArea) {
                                    localityPriceTrendDto.addPriceTrendData(1l, apiPriceTrendData.cityName,epochDataDate, minPricePerUnitArea.longValue());
                                }
                            }

                        }

                AppBus.getInstance().post(new CityPriceTrendEvent(localityPriceTrendDto));
            } else {
                AppBus.getInstance().post(new CityPriceTrendEvent());
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            AppBus.getInstance().post(new CityPriceTrendEvent());
            CommonUtil.TLog(TAG, "Error parsing locality trends", e);
        }

    }

    @Override
    public void onError(ResponseError error) {
        //TODO handle error here
        CityPriceTrendEvent cityPriceTrendEvent = new CityPriceTrendEvent();
        cityPriceTrendEvent.error = error;
        AppBus.getInstance().post(cityPriceTrendEvent);
    }

}

