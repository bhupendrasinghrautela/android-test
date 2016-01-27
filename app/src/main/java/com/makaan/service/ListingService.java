package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.serp.BaseSerpCallback;
import com.makaan.event.serp.GroupSerpCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ListingService implements MakaanService {


    public void handleSerpRequest(Selector serpSelector) {
        handleSerpRequest(serpSelector, null, false);
    }


    public void handleSerpRequest(Selector serpSelector, boolean needGroupings) {
        handleSerpRequest(serpSelector, null, needGroupings);
    }

    public void handleSerpRequest(String selector) {
        handleSerpRequest(selector, null);
    }


    public void handleSerpRequest(Selector serpSelector, String gpId, boolean needGroupings) {
        String serpDetailsURL = ApiConstants.LISTING.concat("?").concat(serpSelector.build());
        if (null != gpId) {
            serpDetailsURL = serpDetailsURL.concat("&gpid").concat(gpId);
        }
        MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        if (needGroupings) {
            // TODO check if we can restrict to needed fields
            Selector groupSelector = serpSelector.clone();
            groupSelector.field("groupingAttributes").field("groupedListings").field("listing").field("id");
            serpDetailsURL = ApiConstants.LISTING.concat("?").concat(groupSelector.build());
            serpDetailsURL = serpDetailsURL.concat("&sourceDomain=Makaan&group=true");
            MakaanNetworkClient.getInstance().get(serpDetailsURL, new GroupSerpCallback());
        }
    }


    public void handleChildSerpRequest(Selector serpSelector, long id) {
        if (null != serpSelector) {
            String serpDetailsURL = ApiConstants.SIMILAR_LISTING.concat(String.valueOf(id)).concat("?").concat(serpSelector.build());
            MakaanNetworkClient.getInstance().get(serpDetailsURL, new BaseSerpCallback());
        }
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

    /**
     * https://marketplace-qa.makaan-ws.com/app/v1/listing/323996?selector={fields: ["avgPriceRisePercentage","amenitiesIds","contactNumber","registered","contactNumbers","seller","company","user","imageURL","cityId","latitude","longitude","percentageCompletionOnTime", "priceAllInclusive", "type", "altText", "title", "imageType", "absolutePath", "imageTypeId", "URL", "percentageCompletionOnTime", "projectStatusCount", "tenantType", "noOfOpenSides", "studyRoom", "poojaRoom", "servantRoom", "balcony", "bedrooms", "bathrooms", "possessionDate", "facingId", "maxConstructionCompletionDate", "minConstructionCompletionDate", "unitName", "unitType", "livabilityScore", "url", "availability", "size", "unitTypeId", "percentageCompletionOnTime", "establishedDate", "description", "poojaRoom", "servantRoom", "mainEntryRoadWidth", "tenantTypes", "derivedAvailability", "projectStatus", "listingCategory", "id", "label", "name", "floor", "securityDeposit", "propertyId", "images", "furnished", "ownershipTypeId", "viewDirections", "viewType", "bookingAmount", "pricePerUnitArea", "price", "currentListingPrice", "totalFloors", "project", "property", "builder", "locality", "suburb", "city", "specifications", "furnishings", "amenity", "projectAmenityId", "projectId", "amenityDisplayName", "listingAmenities", "amenityMaster", "verified", "amenityId", "amenityName", "abbreviation", "masterSpecification", "masterSpecificationCategory", "masterSpecClassName", "masterSpecId", "masterSpecCatId", "masterSpecCatDisplayName", "masterSpecParentCat", "masterSpecParentCatId", "masterSpecParentDisplayName", "masterFurnishingId", "masterFurnishing", "statusId", "sellerId", "ownerId", "seller", "rating", "assist", "fullName", "carParkingType", "noOfCarParks", "negotiable"]}&sourceDomain=Makaan
     */

    public void getListingDetail(Long listingId) {

        if (null != listingId) {
            Selector listingDetailSelector = new Selector();
            listingDetailSelector.fields(new String[]{"companySeller", "avgPriceRisePercentage", "amenitiesIds", "contactNumber", "registered", "contactNumbers", "seller", "company", "user", "imageURL", "cityId", "latitude", "longitude", "percentageCompletionOnTime", "priceAllInclusive", "type", "altText", "title", "imageType", "absolutePath", "imageTypeId", "URL", "percentageCompletionOnTime", "projectStatusCount", "tenantType", "noOfOpenSides", "studyRoom", "poojaRoom", "servantRoom", "balcony", "bedrooms", "bathrooms", "possessionDate", "facingId", "maxConstructionCompletionDate", "minConstructionCompletionDate", "unitName", "unitType", "livabilityScore", "url", "availability", "size", "unitTypeId", "percentageCompletionOnTime", "establishedDate", "description", "poojaRoom", "servantRoom", "mainEntryRoadWidth", "tenantTypes", "derivedAvailability", "projectStatus", "listingCategory", "id", "label", "name", "floor", "securityDeposit", "propertyId", "images", "furnished", "ownershipTypeId", "viewDirections", "viewType", "bookingAmount", "pricePerUnitArea", "price", "currentListingPrice", "totalFloors", "project", "property", "builder", "locality", "suburb", "city", "specifications", "furnishings", "amenity", "projectAmenityId", "projectId", "amenityDisplayName", "listingAmenities", "amenityMaster", "verified", "amenityId", "amenityName", "abbreviation", "masterSpecification", "masterSpecificationCategory", "masterSpecClassName", "masterSpecId", "masterSpecCatId", "masterSpecCatDisplayName", "masterSpecParentCat", "masterSpecParentCatId", "masterSpecParentDisplayName", "masterFurnishingId", "masterFurnishing", "statusId", "sellerId", "ownerId", "seller", "rating", "assist", "fullName", "carParkingType", "noOfCarParks", "negotiable"});

            String listingDetailUrl = ApiConstants.LISTING.concat(listingId.toString()).concat("?").concat(listingDetailSelector.build());
            Type listingDetailType = new TypeToken<ListingDetail>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(listingDetailUrl, listingDetailType, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    ListingDetail listingDetail = (ListingDetail) responseObject;

                    //listingDetail.description = AppUtils.stripHtml(listingDetail.description);
                    AppBus.getInstance().post(new ListingByIdGetEvent(listingDetail));
                }
            });

        }

    }

}
