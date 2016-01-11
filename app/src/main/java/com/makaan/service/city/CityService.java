package com.makaan.service.city;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.city.GetCityByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.city.City;
import com.makaan.service.MakaanService;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 09/01/16.
 */
public class CityService implements MakaanService {


    public void getCityById(Long cityId) {

        if (null != cityId) {
            Selector citySelector = new Selector();
            citySelector.fields(new String[]{ENTITY_DESCRIPTIONS, CENTER_LAT, CENTER_LONG, DESCRIPTION, CITY_HEROSHOT_IMAGE_URL, ANNUAL_GROWTH, RENTAL_YIELD, DEMAND_RATE, SUPPLY_RATE, LABEL});

            String cityUrl = ApiConstants.CITY.concat(cityId.toString()).concat("?").concat(citySelector.build());

            Type cityType = new TypeToken<City>() {}.getType();

            MakaanNetworkClient.getInstance().get(cityUrl, cityType, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    City city = (City) responseObject;
                    AppBus.getInstance().post(new GetCityByIdEvent(city));
                }
            });
        }


    }

}
