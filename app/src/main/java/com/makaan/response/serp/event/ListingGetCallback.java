package com.makaan.response.serp.event;

import android.util.Log;

import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.serp.LisitingPostedBy;
import com.makaan.response.serp.Listing;
import com.makaan.response.serp.ListingData;
import com.makaan.response.serp.ListingImage;
import com.makaan.response.serp.Project;

import static com.makaan.constants.ResponseConstants.*;
import static com.makaan.util.AppUtils.*;
import static com.makaan.util.ListingUtil.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by vaibhav on 24/12/15.
 * <p/>
 * parser for listing get callback
 */
public class ListingGetCallback extends JSONGetCallback {
    public static final String TAG = ListingGetCallback.class.getSimpleName();

    public static final String ACTUAL_PROPERTY_STATUS = "ACTUAL",
            POSTED_BY_BROKER = "BROKER",
            POSTED_BY_OWNER = "PRIVATEEQUITY",
            POSTED_BY_BUILDER = "BUILDER",
            SIMILARITY_TYPE_DISTANCE = "DISTANCE",
            NO_LISITINGS_AVAILABLE = "No Lisitings available",
            BHK_STR = " BHK",
            DAYS = " days";


    @Override
    public void onSuccess(JSONObject apiResponse) {
        ListingGetEvent listingGetEvent = new ListingGetEvent();

        if (null != apiResponse) {
            try {
                ListingData listingData = new ListingData();
                //listingData.totalCount = apiResponse.optInt(TOTAL_COUNT);
                listingGetEvent.listingData = listingData;

                JSONArray items = apiResponse.getJSONArray(ITEMS);
                if (null == items || items.length() == 0) {
                    listingGetEvent.message = NO_LISITINGS_AVAILABLE;
                } else {


                    for (int i = 0; i < items.length(); i++) {
                        System.out.println(("Parsing : " + Integer.valueOf(i).toString()));
                        JSONObject apiListing = items.getJSONObject(i);

                        Listing listing = getListingFromJson(apiListing);
                        listingData.listings.add(listing);

                        listingData.cityName = listing.cityName;
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse lisiting data", e);
            }

        } else {
            listingGetEvent.message = NO_LISITINGS_AVAILABLE;

        }

        System.out.println("End");


        //AppBus.getInstance().post(listingGetEvent);

    }

    private Listing getListingFromJson(JSONObject apiListing) {
        Listing listing = new Listing();
        MasterDataCache masterDataCache = MasterDataCache.getInstance();

        try {

            apiListing = apiListing.getJSONObject("listing");


            if (null != apiListing) {
                JSONObject currentListingPrice = apiListing.getJSONObject(CURR_LISTING_PRICE);
                JSONObject property = apiListing.optJSONObject(PROPERTY);
                JSONObject project = property != null ? property.optJSONObject(PROJECT) : null;
                JSONObject locality = project != null ? project.optJSONObject(LOCALITY) : null;
                JSONObject builder = project != null ? project.optJSONObject(BUILDER) : null;

                JSONObject suburb = locality != null ? locality.optJSONObject(SUBURB) : null;
                JSONObject city = suburb != null ? suburb.optJSONObject(CITY) : null;

                if (null != currentListingPrice && null != property &&
                        null != project && null != locality && null != suburb && null != city) {

                    JSONObject seller = apiListing.optJSONObject(COMPANY_SELLER);
                    JSONObject sellerCompany = null != seller ? seller.optJSONObject(COMPANY) : null;
                    JSONArray images = apiListing.optJSONArray(IMAGES);


                    listing.postedDate = getDateStringFromEpoch(apiListing.optString(POSTED_DATE));
                    listing.lisitingId = apiListing.optInt(ID);

                    listing.description = apiListing.optString(DESCRIPTION);
                    listing.description = stripContent(listing.description, 100, true);

                    listing.bedrooms = property.optInt(BEDROOMS);
                    listing.bathrooms = property.optInt(BATHROOMS);

                    ApiIntLabel propertyType = masterDataCache.getPropertyType(property.optInt(UNIT_TYPE_ID));
                    listing.propertyType = null != propertyType ? propertyType.name : null;

                    ApiIntLabel propertyStatus1 = masterDataCache.getPropertyStatus(apiListing.optInt(CONS_STATUS_ID));
                    listing.propertyStatus = null != propertyStatus1 ? propertyStatus1.name : null;

                    listing.size = property.optInt(SIZE);
                    listing.measure = property.optString(MEASURE);
                    listing.sizeInfo = listing.size > 0 ? listing.size.toString().concat(" ").concat(listing.measure) : null;


                    listing.studyRoom = property.optInt(STUDY_ROOM);
                    listing.servantRoom = property.optInt(SERVANT_ROOM);
                    listing.poojaRoom = property.optInt(POOJA_ROOM);

                    buildBHKInfo(listing, property);

                    listing.latitude = project.optDouble(LATITUDE);
                    listing.longitude = project.optDouble(LONGITUDE);


                    listing.pricePerUnitArea = currentListingPrice.optInt(PRICE_PER_UNIT_AREA);
                    listing.price = currentListingPrice.optDouble(PRICE);
                    listing.localityAvgPrice = apiListing.optDouble(LOCALITY_AVG_PRICE);

                    listing.relativeCreateDate = apiListing.optString(CREATED_AT) != null ? getElapsedDaysFromNow(apiListing.optString(CREATED_AT)).toString().concat(" days ago") : null;

                    listing.isReadyToMove = isReadyToMove(apiListing.optInt(CONS_STATUS_ID));
                    String possessionDateStr = apiListing.optString(POSSESSION_DATE);
                    listing.propertyAge = !isBlank(possessionDateStr) ? getElapsedDaysFromNow(possessionDateStr).toString().concat(DAYS) : null;
                    listing.possessionDate = !isBlank(possessionDateStr) ? getMMMYYYYDateStringFromEpoch(possessionDateStr) : null;

                    //TODO: check if we need diff
                    /*if (listing.price != null && listing.localityAvgPrice != null) {
                        listing.listingPriceDiff = listing.price - listing.localityAvgPrice;
                    }*/

                    listing.project = new Project();
                    listing.project.actual = false;
                    String propertyStatus = property.optString(STATUS);
                    //if (null != propertyStatus && propertyStatus.equalsIgnoreCase(ACTUAL_PROPERTY_STATUS)) {
                        listing.project.actual = true;
                        listing.project.name = project.optString(NAME);
                        listing.project.fullName = project.optString(NAME);
                        listing.project.url = project.optString(URL);
                        if (null != builder) {
                            listing.project.builderName = builder.optString(NAME);
                            listing.project.fullName = builder.optString(NAME) + ' ' + project.optString(NAME);
                        }
                   // }

                    listing.localityName = locality.optString(LABEL);
                    listing.suburbName = suburb.optString(LABEL);
                    listing.cityName = city.optString(LABEL);


                    listing.lisitingPostedBy = new LisitingPostedBy();

                    listing.lisitingPostedBy.type = null != sellerCompany ?
                            (sellerCompany.optString(TYPE) != null ? sellerCompany.optString(TYPE) : null) : null;
                    if (null != listing.lisitingPostedBy.type) {

                        if (listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_BROKER)
                                || listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_OWNER)
                                || listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_BUILDER)) {
                            listing.lisitingPostedBy.name = seller.optString(NAME);
                            //listing.lisitingPostedBy.image = sellerCompany.companyImage; //TODO: implement image
                            listing.lisitingPostedBy.rating = Math.round(sellerCompany.optInt(COMPANY_SCORE) * 10) / (10 * 2); // devided by 2 to show rating out of 5
                            listing.lisitingPostedBy.assist = sellerCompany.getBoolean(ASSIST);
                        }
                    }


                    listing.hasOffer = apiListing.optString(IS_OFFERED) != null && apiListing.getBoolean(IS_OFFERED);

                    //listing.currentServerTime = (new Date()).valueOf();

                    if (null != images) {

                        for (int k = 0; k < images.length(); k++) {
                            JSONObject image = images.getJSONObject(k);
                            ListingImage listingImage = new ListingImage();
                            listing.images.add(listingImage);
                            listingImage.title = image.optString(TITLE);
                            listingImage.altText = image.optString(ALT_TEXT);
                            listingImage.url = image.optString(ABSOLUTE_PATH);
                        }
                    }

                    listing.floor = apiListing.optInt(FLOOR);
                    listing.totalFloors = apiListing.optInt(TOTAL_FLOORS);

                    if (null != apiListing.optString(FACING_ID)) {
                        //listing.facing = apiListing.facing.direction;   //TODO: get direction and translate facing id
                    }

                    listing.furnished = masterDataCache.translateApiLabel(apiListing.optString(FURNISHED));


                    if (null != apiListing.optString(LANDMARK_DISTANCE)) {
                        listing.landMarkDistance = Math.round(apiListing.optDouble(LANDMARK_DISTANCE) * 10) / 10;
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse lisiting data", e);
        }

        return listing;
    }

    private void buildBHKInfo(Listing listing, JSONObject property) {
        String unitTypeString = property.optString(UNIT_TYPE);

        listing.bhkInfo = listing.bedrooms > 0 ? listing.bedrooms.toString().concat(BHK_STR) : null;
        listing.bhkInfo = listing.bhkInfo != null ? listing.bhkInfo.concat(unitTypeString) : null;


    }


    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("/Users/vaibhav/proptiger/MarketPlaceBuyer/app/src/main/mock/listingResponse.json"));
        StringBuilder json  = new StringBuilder();
        String line = null;
        while((line = br.readLine()) !=null ){
            json.append(line);
        }

        JSONObject jsonObject = new JSONObject(json.toString());
        JSONObject data = jsonObject.getJSONObject(ResponseConstants.DATA);

        ListingGetCallback listingGetCallback = new ListingGetCallback();
        listingGetCallback.onSuccess(data);


    }
}
