package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.listing.ListingByIdsGetEvent;
import com.makaan.event.listing.SimilarListingGetEvent;
import com.makaan.event.serp.BaseSerpCallback;
import com.makaan.event.serp.GroupSerpCallback;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.network.StringRequestCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.ResponseError;
import com.makaan.response.listing.ListingOtherSellersCallback;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.RequestConstants.BATHROOMS;
import static com.makaan.constants.RequestConstants.BEDROOMS;
import static com.makaan.constants.RequestConstants.POOJA_ROOM;
import static com.makaan.constants.RequestConstants.PROJECT_ID;
import static com.makaan.constants.RequestConstants.SERVANT_ROOM;
import static com.makaan.constants.RequestConstants.STUDY_ROOM;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void handleSerpRequest(Selector serpSelector) {
        handleSerpRequest(serpSelector, null, false, null);
    }


    public void handleSerpRequest(Selector serpSelector, boolean needGroupings, Selector groupSelector) {
        handleSerpRequest(serpSelector, null, needGroupings, groupSelector);
    }

    public void handleSerpRequest(String selector) {
        handleSerpRequest(selector, null);
    }


    public void handleSerpRequest(Selector serpSelector, String gpId, boolean needGroupings, Selector groupSelector) {
        String serpDetailsURL = ApiConstants.LISTING.concat("?").concat(serpSelector.build());
        if (null != gpId) {
            serpDetailsURL = serpDetailsURL.concat("&gpid=").concat(gpId);
        }
        MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        if (needGroupings) {
            serpDetailsURL = ApiConstants.LISTING.concat("?").concat(groupSelector.build());

            if (null != gpId) {
                serpDetailsURL = serpDetailsURL.concat("&gpid=").concat(gpId);
            }

            serpDetailsURL = serpDetailsURL.concat("&group=true&sourceDomain=Makaan");
            MakaanNetworkClient.getInstance().get(serpDetailsURL, new GroupSerpCallback());
        }
    }


    public void handleChildSerpRequest(Selector serpSelector, long id) {
        if (null != serpSelector) {
            String serpDetailsURL = ApiConstants.SIMILAR_LISTING.concat(String.valueOf(id)).concat("?").concat(serpSelector.build());
            MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        }
    }

    /**
     * https://marketplace-qa.makaan-ws.com/app/v1/listing/323996?selector={fields: ["avgPriceRisePercentage","amenitiesIds","contactNumber","registered","contactNumbers","seller","company","user","imageURL","cityId","latitude","longitude","percentageCompletionOnTime", "priceAllInclusive", "type", "altText", "title", "imageType", "absolutePath", "imageTypeId", "URL", "percentageCompletionOnTime", "projectStatusCount", "tenantType", "noOfOpenSides", "studyRoom", "poojaRoom", "servantRoom", "balcony", "bedrooms", "bathrooms", "possessionDate", "facingId", "maxConstructionCompletionDate", "minConstructionCompletionDate", "unitName", "unitType", "livabilityScore", "url", "availability", "size", "unitTypeId", "percentageCompletionOnTime", "establishedDate", "description", "poojaRoom", "servantRoom", "mainEntryRoadWidth", "tenantTypes", "derivedAvailability", "projectStatus", "listingCategory", "id", "label", "name", "floor", "securityDeposit", "propertyId", "images", "furnished", "ownershipTypeId", "viewDirections", "viewType", "bookingAmount", "pricePerUnitArea", "price", "currentListingPrice", "totalFloors", "project", "property", "builder", "locality", "suburb", "city", "specifications", "furnishings", "amenity", "projectAmenityId", "projectId", "amenityDisplayName", "listingAmenities", "amenityMaster", "verified", "amenityId", "amenityName", "abbreviation", "masterSpecification", "masterSpecificationCategory", "masterSpecClassName", "masterSpecId", "masterSpecCatId", "masterSpecCatDisplayName", "masterSpecParentCat", "masterSpecParentCatId", "masterSpecParentDisplayName", "masterFurnishingId", "masterFurnishing", "statusId", "sellerId", "ownerId", "seller", "rating", "assist", "fullName", "carParkingType", "noOfCarParks", "negotiable"]}&sourceDomain=Makaan
     */

    public void getListingDetail(Long listingId) {

        if (null != listingId) {
            Selector listingDetailSelector = new Selector();
            listingDetailSelector.fields(new String[]{"logo", "profilePictureURL", "score", "masterAmenityIds", "companySeller", "overviewUrl", "constructionStatusId", "mainImage", "activeStatus", "mainImageURL", "avgPriceRisePercentage", "amenitiesIds", "contactNumber", "registered", "contactNumbers", "seller", "company", "user", "imageURL", "cityId", "latitude", "longitude", "percentageCompletionOnTime", "priceAllInclusive", "type", "altText", "title", "imageType", "absolutePath", "imageTypeId", "URL", "percentageCompletionOnTime", "projectStatusCount", "tenantType", "noOfOpenSides", "studyRoom", "poojaRoom", "servantRoom", "balcony", "bedrooms", "bathrooms", "possessionDate", "facingId", "maxConstructionCompletionDate", "minConstructionCompletionDate", "unitName", "unitType", "livabilityScore", "url", "availability", "size", "unitTypeId", "percentageCompletionOnTime", "establishedDate", "description", "poojaRoom", "servantRoom", "mainEntryRoadWidth", "tenantTypes", "derivedAvailability", "projectStatus", "listingCategory", "id", "label", "name", "floor", "securityDeposit", "propertyId", "images", "furnished", "ownershipTypeId", "viewDirections", "viewType", "bookingAmount", "pricePerUnitArea", "avgPricePerUnitArea", "price", "currentListingPrice", "totalFloors", "project", "property", "builder", "locality", "localityId", "suburb", "city", "specifications", "furnishings", "amenity", "projectAmenityId", "projectId", "amenityDisplayName", "listingAmenities", "amenityMaster", "verified", "amenityId", "amenityName", "abbreviation", "masterSpecification", "masterSpecificationCategory", "masterSpecClassName", "masterSpecId", "masterSpecCatId", "masterSpecCatDisplayName", "masterSpecParentCat", "masterSpecParentCatId", "masterSpecParentDisplayName", "masterFurnishingId", "masterFurnishing", "statusId", "sellerId", "ownerId", "seller", "rating", "assist", "fullName", "carParkingType", "noOfCarParks", "negotiable"});
            String listingDetailUrl = ApiConstants.LISTING.concat(listingId.toString()).concat("?").concat(listingDetailSelector.build());
            Type listingDetailType = new TypeToken<ListingDetail>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(listingDetailUrl, listingDetailType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    ListingByIdGetEvent listingByIdGetEvent = new ListingByIdGetEvent();
                    listingByIdGetEvent.error = error;
                    AppBus.getInstance().post(listingByIdGetEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    ListingDetail listingDetail = (ListingDetail) responseObject;

                    //listingDetail.description = AppUtils.stripHtml(listingDetail.description);
                    AppBus.getInstance().post(new ListingByIdGetEvent(listingDetail));
                }
            });

        }

    }

    public void getListingDetailByIds(ArrayList<String> listingIds) {
        if (null != listingIds) {
            Selector selector = new Selector();
            selector.field("user").field("mainImageURL").field("logo").field("profilePictureURL").field("currentListingPrice")
                    .field("price").field("projectId").field("companySeller").field("company")
                    .field("name").field("score").field("halls").field("unitType")
                    .field("unitName").field("measure").field("size").field("bathrooms")
                    .field("bedrooms").field("listing").field("id").field("property")
                    .field("project").field("builder").field("locality").field("suburb")
                    .field("label").field("city").field("imageURL");

            selector.term("listingId", listingIds);
            String listingDetailUrl = ApiConstants.LISTING.concat("?").concat(selector.build());
            Type listingDetailType = new TypeToken<ListingByIdsGetEvent>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(listingDetailUrl, new JSONGetCallback() {
                @Override
                public void onError(ResponseError error) {

                    ListingByIdsGetEvent listingByIdsGetEvent = new ListingByIdsGetEvent();
                    listingByIdsGetEvent.error = error;
                    AppBus.getInstance().post(listingByIdsGetEvent);
                }

                @Override
                public void onSuccess(JSONObject responseObject) {
                    if (responseObject != null) {
                        try {
                            JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                            Type type = new TypeToken<ListingByIdsGetEvent>() {
                            }.getType();
                            ListingByIdsGetEvent listingByIdsGetEvent = MakaanBuyerApplication.gson.fromJson(data.toString(), type);
                            AppBus.getInstance().post(listingByIdsGetEvent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

    }


    public void getSimilarListingDetail(Long listingId) {
        if (null != listingId) {
            Selector listingDetailSelector = new Selector();
            listingDetailSelector.fields(new String[]{"assist", "company", "companySeller", "mainImageURL", "resaleURL", "overviewUrl", "listing", "size", "isAssist", "carpetArea", "currentListingPrice", "unitName", "url", "id", "label", "name", "unitTypeId", "bedrooms", "type", "altText", "title", "carpetArea", "priceVerified", "imageType", "absolutePath", "pricePerUnitArea", "price", "currentListingPrice", "project", "property", "builder", "locality", "suburb", "city", "amenity", "verified"});

            String listingDetailUrl = ApiConstants.SIMILAR_LISTING.concat(listingId.toString()).concat("?").concat(listingDetailSelector.build());

            MakaanNetworkClient.getInstance().get(listingDetailUrl, new StringRequestCallback() {
                @Override
                public void onError(ResponseError error) {
                    SimilarListingGetEvent similarListingGetEvent = new SimilarListingGetEvent();
                    similarListingGetEvent.error = error;
                    AppBus.getInstance().post(similarListingGetEvent);
                }

                @Override
                public void onSuccess(String responseObject) {
                    SimilarListingGetEvent similarListingGetEvent = (SimilarListingGetEvent) JsonParser.parseJson(responseObject, SimilarListingGetEvent.class);

                    //listingDetail.description = AppUtils.stripHtml(listingDetail.description);
                    AppBus.getInstance().post(similarListingGetEvent);
                }
            });

        }
    }


    public void getListingDetailForEnquiry(Long listingId) {
        if (null != listingId) {
            Selector listingDetailSelector = new Selector();
            listingDetailSelector.field("user").field("mainImageURL").field("logo").field("profilePictureURL").field("currentListingPrice")
                    .field("price").field("projectId").field("companySeller").field("company")
                    .field("name").field("score").field("halls").field("unitType").field("userId")
                    .field("unitName").field("measure").field("size").field("bathrooms")
                    .field("bedrooms").field("listing").field("id").field("property")
                    .field("project").field("builder").field("locality").field("suburb")
                    .field("label").field("city").field("imageURL").field("latitude").field("longitude");
            String listingDetailUrl = ApiConstants.LISTING.concat(listingId.toString()).concat("?").concat(listingDetailSelector.build());
            Type listingDetailType = new TypeToken<ListingDetail>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(listingDetailUrl, listingDetailType, new ObjectGetCallback() {
                @Override
                public void onError(ResponseError error) {
                    ListingByIdGetEvent listingByIdGetEvent = new ListingByIdGetEvent();
                    listingByIdGetEvent.error = error;
                    AppBus.getInstance().post(listingByIdGetEvent);
                }

                @Override
                public void onSuccess(Object responseObject) {
                    ListingDetail listingDetail = (ListingDetail) responseObject;

                    //listingDetail.description = AppUtils.stripHtml(listingDetail.description);
                    AppBus.getInstance().post(new ListingByIdGetEvent(listingDetail));
                }
            });

        }
    }


    /**
     * http:/marketplace-qa.makaan-ws.com/data/v2/entity/domain?selector={"fields":["companyImage","score","contactNumber","contactNumbers","user","name","id","label","sellerId","property","currentListingPrice","price","bedrooms","bathrooms","size","unitTypeId","project","projectId","studyRoom","servantRoom","poojaRoom","companySeller","company","companyScore"],"filters":{"and":[{"equal":{"projectId":["654368"]}},{"equal":{"bedrooms":["3"]}},{"equal":{"bathrooms":["3"]}},{"equal":{"studyRoom":["0"]}},{"equal":{"poojaRoom":["0"]}},{"equal":{"servantRoom":["0"]}}]},"paging":{"start":"0","rows":"5"},"groupBy":{"field":"sellerId","min":"listingSellerCompanyScore"}}&documentType=LISTING&facets=bedrooms,sellerId&sourceDomain=Makaan
     */

    public void getOtherSellersOnListingDetail(Long projectId, Integer bedrooms, Integer bathrooms, Integer studyRoom, Integer poojaRoom, Integer servantRoom, Integer noOfSellers) {

        Selector otherSellersSelector = new Selector();
        otherSellersSelector.fields(new String[]{"companyImage", "score", "contactNumber", "contactNumbers", "user", "name", "id", "logo", "profileImageURL", "label", "sellerId", "property", "currentListingPrice", "price", "bedrooms", "bathrooms", "size", "unitTypeId", "project", "projectId", "studyRoom", "servantRoom", "poojaRoom", "companySeller", "company", "companyScore", "type"});
        if (null != projectId) {
            otherSellersSelector.term(PROJECT_ID, projectId.toString());
        }
        if (null != bedrooms) {
            otherSellersSelector.term(BEDROOMS, bedrooms.toString());
        }
        if (null != bathrooms) {
            otherSellersSelector.term(BATHROOMS, bathrooms.toString());
        }
        if (null != studyRoom) {
            otherSellersSelector.term(STUDY_ROOM, studyRoom.toString());
        }
        if (null != poojaRoom) {
            otherSellersSelector.term(POOJA_ROOM, poojaRoom.toString());
        }
        if (null != servantRoom) {
            otherSellersSelector.term(SERVANT_ROOM, servantRoom.toString());
        }
        if (null != noOfSellers) {
            otherSellersSelector.page(0, noOfSellers);
        } else {
            otherSellersSelector.page(0, 5);
        }
        otherSellersSelector.groupBy("sellerId", "listingSellerCompanyScore");

        StringBuilder otherSellersUrl = new StringBuilder(ApiConstants.LISTING_OTHER_SELLERS);
        otherSellersUrl.append("?").append(otherSellersSelector.build()).append("&documentType=LISTING&facets=bedrooms,sellerId");

        MakaanNetworkClient.getInstance().get(otherSellersUrl.toString(), new ListingOtherSellersCallback());
    }


    public void handleSerpRequest(String selector, String gpId) {
        if (null != selector) {
            String serpDetailsURL = ApiConstants.LISTING.concat("?selector={\"filters\":").concat(selector).concat("}");
            if (null != gpId) {
                serpDetailsURL = serpDetailsURL.concat("&gpid").concat(gpId);
            }
            MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        }

    }

}
