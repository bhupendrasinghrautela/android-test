package com.makaan.event.trend.callback;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.trend.ApiPriceTrendData;
import com.makaan.response.trend.LocalityPriceTrendDto;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.makaan.constants.ResponseConstants.MIN_PRICE_PER_UNIT_AREA;

/**
 * Created by vaibhav on 13/01/16.
 *
 */
public abstract class LocalityTrendCallback extends JSONGetCallback {

    public static final String TAG = LocalityTrendCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {

        LocalityPriceTrendDto localityPriceTrendDto = new LocalityPriceTrendDto();

        HashMap<Long, HashMap<String, List<ApiPriceTrendData>>> data ;
        Type apiPriceTrendType = new TypeToken<HashMap<Long, HashMap<String, List<ApiPriceTrendData>>>>() {}.getType();

        try {
            data = MakaanBuyerApplication.gson.fromJson(dataResponse.getJSONObject(ResponseConstants.DATA).toString(), apiPriceTrendType);


            if (null != data) {
                for (Map.Entry<Long, HashMap<String, List<ApiPriceTrendData>>> entry : data.entrySet()) {
                    Long localityId = entry.getKey();
                    HashMap<String, List<ApiPriceTrendData>> localityTrendData = entry.getValue();

                    if (null != localityTrendData) {
                        for (Map.Entry<String, List<ApiPriceTrendData>> trendEntry : localityTrendData.entrySet()) {
                            Long epochDataDate = Long.valueOf(trendEntry.getKey());
                           // Date dataDate = AppUtils.getDateFromEpoch(epochDataDate);

                            List<ApiPriceTrendData> apiPriceTrendList = trendEntry.getValue();
                            if (null != apiPriceTrendList && apiPriceTrendList.size() > 0) {
                                ApiPriceTrendData apiPriceTrendData = apiPriceTrendList.get(0);
                                Long minPricePerUnitArea = apiPriceTrendData.extraAttributes.get(MIN_PRICE_PER_UNIT_AREA);
                                if (null != minPricePerUnitArea) {
                                    localityPriceTrendDto.addPriceTrendData(localityId, apiPriceTrendData.localityName,epochDataDate, minPricePerUnitArea);
                                }
                            }

                        }

                    }

                }
                onTrendReceived(localityPriceTrendDto);
            } else {
                onTrendReceived(null);
            }
        }catch (Exception e){
            Log.e(TAG, "Error parsing locality trends", e);
        }

    }

    @Override
    public void onError(ResponseError error) {
        //TODO handle error here
    }

    public abstract void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto);


}
