package com.makaan.service.city;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.city.GetCityByIdEvent;
import com.makaan.event.city.GetCityTopLocalityEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.city.City;
import com.makaan.response.locality.Locality;
import com.makaan.service.MakaanService;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 09/01/16.
 */
public class CityService implements MakaanService {


    /**
     * https://marketplace-qa.proptiger-ws.com/app/v1/city/2?selector={"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}}
     *
     */
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

    /**
     *  http:/marketplace-qa.proptiger-ws.com/data/v3/entity/locality?selector={"filters":{"and":[{"equal":{"cityId":"2"}}]},"paging":{"start":0,"rows":5},"sort":[{"field":"localityPriority","sortOrder":"DESC"}]}
     *
     */
    public void getTopLocalitiesInCity(Long cityId, Integer noOfTopLocalities){

        if(null != cityId){

            Selector topLocaliltySelector = new Selector();
            topLocaliltySelector.term(CITY, cityId.toString()).page(0, noOfTopLocalities != null ? noOfTopLocalities: 5)
                    .sort(LOCALITY_PRIORITY, SORT_DESC);

            Type topLocalitiesListType = new TypeToken<ArrayList<Locality>>(){}.getType();

            String topLocalitiesUrl = ApiConstants.LOCALITY_DATA.concat("?").concat(topLocaliltySelector.build());

            MakaanNetworkClient.getInstance().get(topLocalitiesUrl, topLocalitiesListType, new ObjectGetCallback() {
                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<Locality> topLocalities = (ArrayList<Locality>) responseObject;
                    AppBus.getInstance().post(new GetCityTopLocalityEvent(topLocalities));
                }
            });
        }
    }

    /**
     * http://marketplace-qa.makaan-ws.com/data/v1/trend/hitherto?fields=minPricePerUnitArea,localityName,projectName&filters=localityId==51549,localityId==51751,localityId==53250,localityId==53133&monthDuration=6&group=localityId,month
     */

    public void getPriceTrendForTopLocalitiesInCity(){

    }



}
