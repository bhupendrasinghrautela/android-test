package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.city.CityTrendCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.city.City;
import com.makaan.response.locality.ListingAggregation;
import com.makaan.response.locality.Locality;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.BEDROOMS;
import static com.makaan.constants.RequestConstants.CITY_ID;
import static com.makaan.constants.RequestConstants.LISTING_CATEGORY;
import static com.makaan.constants.RequestConstants.LOCALITY_LIVABILITY_SCORE;
import static com.makaan.constants.RequestConstants.PRIMARY;
import static com.makaan.constants.RequestConstants.RENTAL;
import static com.makaan.constants.RequestConstants.RESALE;
import static com.makaan.constants.RequestConstants.SORT_DESC;
import static com.makaan.constants.RequestConstants.UNIT_TYPE_ID;


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

            //citySelector.fields(new String[]{ID, ENTITY_DESCRIPTIONS, CITY_TAG_LINE, CENTER_LAT, CENTER_LONG, DESCRIPTION, CITY_HEROSHOT_IMAGE_URL, ANNUAL_GROWTH, RENTAL_YIELD, DEMAND_RATE, SUPPLY_RATE, LABEL});

            citySelector.fields(new String[]{"id","centerLatitude","centerLongitude","description","cityHeroshotImageUrl","annualGrowth","rentalYield","demandRate","supplyRate","label","listingAggregations","buyUrl","rentUrl","entityDescriptions","description","entityDescriptionCategories","masterDescriptionCategory","name","images","imageType","absolutePath","displayName","masterDescriptionParentCategories","parentCategory","cityTagLine"});


            String cityUrl = ApiConstants.CITY.concat(cityId.toString()).concat("?").concat(citySelector.build());

            Type cityType = new TypeToken<City>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(cityUrl, cityType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    CityByIdEvent cityByIdEvent = new CityByIdEvent();
                    cityByIdEvent.error = error;
                    AppBus.getInstance().post(cityByIdEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    City city = (City) responseObject;
                    //city.description = AppUtils.stripHtml(city.description);
                    getMinMaxPrice(city);
                    AppBus.getInstance().post(new CityByIdEvent(city));
                }
            });
        }


    }

    private void getMinMaxPrice(City city) {
        ArrayList<ListingAggregation> listingAggregations = city.listingAggregations;
        if(listingAggregations!=null && listingAggregations.size()>0){
            for(ListingAggregation listingAggregation:listingAggregations){
                if(listingAggregation.listingCategory!=null) {
                    if (listingAggregation.listingCategory.toLowerCase().equals("primary")
                            || listingAggregation.listingCategory.toLowerCase().equals("resale")) {
                        if (city.cityBuyMinPrice == null && listingAggregation.minPrice>0) {
                            city.cityBuyMinPrice = listingAggregation.minPrice;
                        } else if (city.cityBuyMinPrice > listingAggregation.minPrice && listingAggregation.minPrice>0) {
                            city.cityBuyMinPrice = listingAggregation.minPrice;
                        }
                        if (city.cityBuyMaxPrice == null) {
                            city.cityBuyMaxPrice = listingAggregation.maxPrice;
                        } else if (city.cityBuyMaxPrice < listingAggregation.maxPrice) {
                            city.cityBuyMaxPrice = listingAggregation.maxPrice;
                        }
                    } else {
                        if (city.cityRentMinPrice == null && listingAggregation.minPrice>0) {
                            city.cityRentMinPrice = listingAggregation.minPrice;
                        } else if (city.cityRentMinPrice > listingAggregation.minPrice && listingAggregation.minPrice>0) {
                            city.cityRentMinPrice = listingAggregation.minPrice;
                        }
                        if (city.cityRentMaxPrice == null) {
                            city.cityRentMaxPrice = listingAggregation.maxPrice;
                        } else if (city.cityRentMaxPrice < listingAggregation.maxPrice) {
                            city.cityRentMaxPrice = listingAggregation.maxPrice;
                        }
                    }
                }
            }
        }
        //Hardcoded values for bar as per site
        city.cityBuyMaxPrice = city.cityBuyMaxPrice == null?20000000d:(city.cityBuyMaxPrice>20000000d?20000000d:city.cityBuyMaxPrice);
        city.cityBuyMinPrice = city.cityBuyMinPrice == null?1500000d:(city.cityBuyMinPrice<1500000d?1500000d:city.cityBuyMinPrice);
        city.cityRentMaxPrice = city.cityRentMaxPrice == null?1500000d:(city.cityRentMaxPrice>1500000d?1500000d:city.cityRentMaxPrice);
        city.cityRentMinPrice = city.cityRentMinPrice == null?2000d:(city.cityRentMinPrice<2000d?2000d:city.cityRentMinPrice);
    }

    /**
     * http:/marketplace-qa.proptiger-ws.com/data/v3/entity/locality?selector={"filters":{"and":[{"equal":{"cityId":"2"}}]},"paging":{"start":0,"rows":5},"sort":[{"field":"localityPriority","sortOrder":"DESC"}]}
     */
    public void getTopLocalitiesInCity(Long cityId, Integer noOfTopLocalities) {

        if (null != cityId) {

            Selector topLocaliltySelector = new Selector();
            topLocaliltySelector.term(CITY_ID, cityId.toString()).page(0, noOfTopLocalities != null ? noOfTopLocalities : 15)
                    .sort(LOCALITY_LIVABILITY_SCORE, SORT_DESC);

            Type topLocalitiesListType = new TypeToken<ArrayList<Locality>>() {
            }.getType();

            String topLocalitiesUrl = ApiConstants.LOCALITY_DATA.concat("?").concat(topLocaliltySelector.build());

            MakaanNetworkClient.getInstance().get(topLocalitiesUrl, topLocalitiesListType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    CityTopLocalityEvent cityTopLocalityEvent = new CityTopLocalityEvent();
                    cityTopLocalityEvent.error = error;
                    AppBus.getInstance().post(cityTopLocalityEvent);
                }

                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<Locality> topLocalities = (ArrayList<Locality>) responseObject;
                    AppBus.getInstance().post(new CityTopLocalityEvent(topLocalities));
                }
            }, true);
        }
    }

    /**
     * http:/marketplace-qa.makaan-ws.com/app/v1/listing?selector={"filters":{"and":[{"equal":{"cityId":"2"}}]},"paging":{"start":0,"rows":5}}&facetRanges=[{"field":"price","start":10000,"end":500000,"gap":12250}]
     */

    public void getPropertyRangeInCity(Long cityId, ArrayList<Integer> bedrooms, ArrayList<Integer> propertyTypes, boolean isRental, Integer start, Integer end, Integer gap) {

        if (null != cityId) {
            Selector cityPropertyRangeSel = new Selector();
            cityPropertyRangeSel.term(CITY_ID, cityId.toString()).page(0, 0);
            if (null != bedrooms && bedrooms.size() > 0) {
                for (Integer bed : bedrooms) {
                    cityPropertyRangeSel.term(BEDROOMS, bed.toString());
                }
            }
            if (null != propertyTypes && propertyTypes.size() > 0) {
                for (Integer propertyType : propertyTypes) {
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
