package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.locality.GpByIdEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.locality.TopBuilderInLocalityEvent;
import com.makaan.event.locality.TrendingSearchLocalityEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.locality.GpDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Builder;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.BUILDER_DB_STATUS;
import static com.makaan.constants.RequestConstants.GEO_DISTANCE;
import static com.makaan.constants.RequestConstants.LABEL;
import static com.makaan.constants.RequestConstants.LISTING_AGGREGATIONS;
import static com.makaan.constants.RequestConstants.LOCALITY_HEROSHOT_IMAGE_URL;
import static com.makaan.constants.RequestConstants.LOCALITY_ID;
import static com.makaan.constants.RequestConstants.SORT_ASC;

/**
 * Created by vaibhav on 09/01/16.
 */
public class LocalityService implements MakaanService {

    /**
     *  http:/marketplace-qa.makaan-ws.com/app/v3/locality/50157?selector={"fields":["label","cityId","localityId","livabilityScore","latitude","longitude","description","avgPriceRisePercentage","avgPricePerUnitArea","averageRentPerMonth","description","entityDescriptions","minAffordablePrice","maxAffordablePrice","minLuxuryPrice","maxBudgetPrice","buyUrl","avgRentalDemandRisePercentage","localityHeroshotImageUrl","suburb","city","averageRentPerMonth","cityHeroshotImageUrl","listingAggregations","listingCategory","unitType","bedrooms","entityDescriptions","description","entityDescriptionCategories","masterDescriptionCategory","name","masterDescriptionParentCategories","parentCategory","count","minSize","maxSize","minPrice","maxPrice","minPricePerUnitArea","maxPricePerUnitArea"]}&sourceDomain="Makaan"
     */
    public void getLocalityById(Long localityId) {

        if (null != localityId) {

            String localityUrl = ApiConstants.LOCALITY.concat(localityId.toString());

            Selector localitySelector = new Selector();

            localitySelector.fields(new String[]{"label","images","absolutePath","imageType","displayName", "cityId", "localityId", "livabilityScore", "latitude", "longitude", "description", "avgPriceRisePercentage", "avgPricePerUnitArea", "averageRentPerMonth", "description", "entityDescriptions", "minAffordablePrice", "maxAffordablePrice", "minLuxuryPrice", "maxBudgetPrice", "buyUrl", "avgRentalDemandRisePercentage", "localityHeroshotImageUrl", "suburb", "city", "averageRentPerMonth", "cityHeroshotImageUrl", "listingAggregations", "listingCategory", "unitType", "bedrooms", "entityDescriptions", "description", "entityDescriptionCategories", "masterDescriptionCategory", "name", "masterDescriptionParentCategories", "parentCategory", "count", "minSize", "maxSize", "minPrice", "maxPrice", "minPricePerUnitArea", "maxPricePerUnitArea"});

            localityUrl = localityUrl.concat("?").concat(localitySelector.build());
            Type localityType = new TypeToken<Locality>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(localityUrl, localityType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    LocalityByIdEvent localityByIdEvent = new LocalityByIdEvent();
                    localityByIdEvent.error = error;
                    AppBus.getInstance().post(localityByIdEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    Locality locality = (Locality) responseObject;
                    //locality.description = AppUtils.stripHtml(locality.description);
                    AppBus.getInstance().post(new LocalityByIdEvent(locality));
                }
            });
        }
    }

    /**
     *
     http:/marketplace-qa.makaan-ws.com/data/v3/entity/locality?selector={"fields":["localityId"],"filters":{"and":[{"geoDistance":{"geo":{"distance":10,"lat":"12.84112072","lon":"77.66799164"}}}]},"paging":{"start":0,"rows":3},"sort":[{"field":"geoDistance","sortOrder":"ASC"}]}
     */
    public void getNearByLocalities(double lat, double lon, int noOfLocalities) {

        Selector nearByLocalitySelector = new Selector();
        nearByLocalitySelector.nearby(10, lat, lon, false).fields(new String[]{LOCALITY_ID, LABEL, LISTING_AGGREGATIONS, LOCALITY_HEROSHOT_IMAGE_URL})
                .sort(GEO_DISTANCE, SORT_ASC).page(0, noOfLocalities);

        String nearbyLocalityUrl = ApiConstants.LOCALITY_DATA.concat("?").concat(nearByLocalitySelector.build());

        Type localityListType = new TypeToken<ArrayList<Locality>>() {
        }.getType();

        MakaanNetworkClient.getInstance().get(nearbyLocalityUrl, localityListType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {
                NearByLocalitiesEvent nearByLocalitiesEvent = new NearByLocalitiesEvent();
                nearByLocalitiesEvent.error = error;
                AppBus.getInstance().post(nearByLocalitiesEvent);
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onSuccess(Object responseObject) {
                ArrayList<Locality> nearByLocalities = (ArrayList<Locality>) responseObject;
                NearByLocalitiesEvent nearByLocalitiesEvent = new NearByLocalitiesEvent(nearByLocalities);
                AppBus.getInstance().post(nearByLocalitiesEvent);
            }
        }, true);
    }


    /**
     * http://marketplace-qa.proptiger-ws.com/columbus/app/v1/popular/suggestions?entityId=50175&sourceDomain=Makaan
     */
    public void getTrendingSearchesInLocality(Long localityId) {

        if (null != localityId) {
            String localityTrendingSearch = ApiConstants.COLUMBUS_SUGGESTIONS.concat("?entityId=").concat(localityId.toString());

            Type searchListType = new TypeToken<ArrayList<SearchResponseItem>>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(localityTrendingSearch, searchListType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    TrendingSearchLocalityEvent trendingSearchLocalityEvent = new TrendingSearchLocalityEvent();
                    trendingSearchLocalityEvent.error = error;
                    AppBus.getInstance().post(trendingSearchLocalityEvent);
                }

                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<SearchResponseItem> trendingSearches = (ArrayList<SearchResponseItem>) responseObject;
                    AppBus.getInstance().post(new TrendingSearchLocalityEvent(trendingSearches));
                }
            }, true);

        }
    }

    /**
     * http://marketplace-qa.proptiger-ws.com/data/v1/entity/city/2/top-agents?selector={%20%22filters%22:%20{%20%22and%22:%20[{%22equal%22:%20{%20%22localityId%22:%2050175%20}}]%20}%20}&sourceDomain=Makaan
     *
     */
    public void getTopBuildersInLocality(Long localityId, int noOfBuilder) {

        if (null != localityId) {

            Selector topBuilderSelector = new Selector();

            topBuilderSelector.fields(new String[]{"id", "name", "establishedDate", "projectCount", "images", "absolutePath", "url", "activeStatus", "projectStatusCount"});
            topBuilderSelector.term(LOCALITY_ID, localityId.toString()).term(BUILDER_DB_STATUS, "Active").page(0, noOfBuilder);

            String localityTopBuilder = ApiConstants.TOP_BUILDER.concat("?").concat(topBuilderSelector.build());

            Type builderListType = new TypeToken<ArrayList<Builder>>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(localityTopBuilder, builderListType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    TopBuilderInLocalityEvent topBuilderInLocalityEvent = new TopBuilderInLocalityEvent();
                    topBuilderInLocalityEvent.error = error;
                    AppBus.getInstance().post(topBuilderInLocalityEvent);
                }

                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<Builder> topBuildersInLocality = (ArrayList<Builder>) responseObject;
                    AppBus.getInstance().post(new TopBuilderInLocalityEvent(topBuildersInLocality));
                }
            }, true);

        }
    }

    /**
     * http://mp-qa1.makaan-ws.com/app/v1/gp/place-detail/ChIJAQAA8UjkDDkRmdprFuRlLbo?sourceDomain=Makaan
     */
    public void getGooglePlaceDetail(String gpId) {
        if (null != gpId) {
            String gpDetailUrl = ApiConstants.GP_DETAIL.concat("/").concat(gpId);

            Type gpDetailType = new TypeToken<GpDetail>() {
            }.getType();
            MakaanNetworkClient.getInstance().get(gpDetailUrl, gpDetailType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    GpByIdEvent gpByIdEvent = new GpByIdEvent();
                    gpByIdEvent.error = error;
                    AppBus.getInstance().post(gpByIdEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    GpDetail gpDetail = (GpDetail) responseObject;
                    AppBus.getInstance().post(new GpByIdEvent(gpDetail));
                }
            });
        }
    }


}
