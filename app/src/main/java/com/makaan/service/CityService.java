package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.city.CityTrendCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.city.City;
import com.makaan.response.locality.Locality;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.*;


/**
 * Created by vaibhav on 09/01/16.
 */
public class CityService implements MakaanService {


    /**
     * https://marketplace-qa.proptiger-ws.com/app/v1/city/2?selector={"fields":["entityDescriptions","id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label"],"filters":{}}
     */
    public void getCityById(Long cityId) {

        if (null != cityId) {
            Selector citySelector = new Selector();
            citySelector.fields(new String[]{ID, ENTITY_DESCRIPTIONS, CITY_TAG_LINE, CENTER_LAT, CENTER_LONG, DESCRIPTION, CITY_HEROSHOT_IMAGE_URL, ANNUAL_GROWTH, RENTAL_YIELD, DEMAND_RATE, SUPPLY_RATE, LABEL});

            String cityUrl = ApiConstants.CITY.concat(cityId.toString()).concat("?").concat(citySelector.build());

            Type cityType = new TypeToken<City>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(cityUrl, cityType, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    City city = (City) responseObject;
                    city.description = AppUtils.stripHtml(city.description);
                    AppBus.getInstance().post(new CityByIdEvent(city));
                }
            });
        }


    }

    /**
     * http:/marketplace-qa.proptiger-ws.com/data/v3/entity/locality?selector={"filters":{"and":[{"equal":{"cityId":"2"}}]},"paging":{"start":0,"rows":5},"sort":[{"field":"localityPriority","sortOrder":"DESC"}]}
     */
    public void getTopLocalitiesInCity(Long cityId, Integer noOfTopLocalities) {

        if (null != cityId) {

            Selector topLocaliltySelector = new Selector();
            topLocaliltySelector.term(CITY_ID, cityId.toString()).page(0, noOfTopLocalities != null ? noOfTopLocalities : 5)
                    .sort(LOCALITY_PRIORITY, SORT_DESC);

            Type topLocalitiesListType = new TypeToken<ArrayList<Locality>>() {
            }.getType();

            String topLocalitiesUrl = ApiConstants.LOCALITY_DATA.concat("?").concat(topLocaliltySelector.build());

            MakaanNetworkClient.getInstance().get(topLocalitiesUrl, topLocalitiesListType, new ObjectGetCallback() {
                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<Locality> topLocalities = (ArrayList<Locality>) responseObject;
                    AppBus.getInstance().post(new CityTopLocalityEvent(topLocalities));
                }
            }, true);
        }
    }

    public void getPropertyRangeInCity(Long cityId, ArrayList<Long> bedrooms, ArrayList<Long> propertyTypes, boolean isRental, Integer start, Integer end, Integer gap) {

        if (null != cityId) {
            Selector cityPropertyRangeSel = new Selector();
            cityPropertyRangeSel.term(CITY_ID, cityId.toString()).page(0, 0);
            if (null != bedrooms && bedrooms.size() > 0) {
                for (Long bed : bedrooms) {
                    cityPropertyRangeSel.term(BEDROOMS, bed.toString());
                }
            }
            if (null != propertyTypes && propertyTypes.size() > 0) {
                for (Long propertyType : propertyTypes) {
                    cityPropertyRangeSel.term(UNIT_TYPE_ID, propertyType.toString());
                }
            }

            if (isRental) {
                cityPropertyRangeSel.term(LISTING_CATEGORY, RENTAL);
            } else {
                cityPropertyRangeSel.term(LISTING_CATEGORY, new String[]{RESALE, PRIMARY});
            }

            String facetRange = "&facetRanges=[{\"field\":\"price\",\"start\":START,\"end\":END,\"gap\":GAP}]";
            facetRange = facetRange.replaceAll("START", start.toString());
            facetRange = facetRange.replaceAll("END", end.toString());
            facetRange = facetRange.replaceAll("GAP", gap.toString());

            StringBuilder cityPropRangeUrl = new StringBuilder(ApiConstants.LISTING);
            cityPropRangeUrl.append("?").append(cityPropertyRangeSel.build()).append(facetRange);

            MakaanNetworkClient.getInstance().get(cityPropRangeUrl.toString(), new CityTrendCallback());
        }
    }


}
