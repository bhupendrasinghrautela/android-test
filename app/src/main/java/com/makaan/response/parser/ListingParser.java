package com.makaan.response.parser;

import com.crashlytics.android.Crashlytics;
import com.makaan.cache.MasterDataCache;
import com.makaan.response.listing.LisitingPostedBy;
import com.makaan.response.listing.Listing;
import com.makaan.response.listing.ListingImage;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.project.Project;
import com.makaan.util.CommonUtil;
import com.makaan.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.makaan.constants.ResponseConstants.*;

import static com.makaan.util.AppUtils.*;

import static com.makaan.util.ListingUtil.*;

/**
 * Created by vaibhav on 31/12/15.
 *
 * will take the listing object json and parse it to construct Lisitng object
 */

public class ListingParser {

    public static final String TAG = ListingParser.class.getSimpleName();

    public static final String ACTUAL_PROPERTY_STATUS = "ACTUAL",
            POSTED_BY_BROKER = "BROKER",
            POSTED_BY_OWNER = "OWNER",
            POSTED_BY_BUILDER = "BUILDER",
            BHK_STR = " bhk ",
            DAYS = " days";


    public Listing getListingFromJson(JSONObject listingJson) {
        Listing listing = new Listing();
        MasterDataCache masterDataCache = MasterDataCache.getInstance();

        try {

            if (null != listingJson) {
                JSONObject currentListingPrice = listingJson.getJSONObject(CURR_LISTING_PRICE);
                JSONObject property = listingJson.optJSONObject(PROPERTY);
                JSONObject project = property != null ? property.optJSONObject(PROJECT) : null;
                JSONObject locality = project != null ? project.optJSONObject(LOCALITY) : null;
                JSONObject builder = project != null ? project.optJSONObject(BUILDER) : null;

                JSONObject suburb = locality != null ? locality.optJSONObject(SUBURB) : null;
                JSONObject city = suburb != null ? suburb.optJSONObject(CITY) : null;

                if (null != currentListingPrice && null != property &&
                        null != project && null != locality && null != suburb && null != city) {

                    JSONObject seller = listingJson.optJSONObject(COMPANY_SELLER);
                    JSONObject user = null != seller ? seller.optJSONObject(USER) : null;
                    JSONObject sellerCompany = null != seller ? seller.optJSONObject(COMPANY) : null;
                    JSONArray images = listingJson.optJSONArray(IMAGES);
                    JSONArray contactNumbers=null!= seller?user.optJSONArray(CONTACT_NUMBERS):null;

                    listing.postedDate = listingJson.optLong(POSTED_DATE);
                    listing.lisitingId = listingJson.optInt(ID);
                    listing.projectId = project.optInt(PROJECT_ID);

//                    listing.description = listingJson.optString(DESCRIPTION);
//                    listing.description = stripContent(listing.description, 100, true);

                    listing.facing = masterDataCache.getDirection(listingJson.optInt(FACING_ID));
                    listing.ownershipType = masterDataCache.getOwnershipType(listingJson.optInt(OWNERSHIP_TYPE_ID));

                    listing.bedrooms = property.optInt(BEDROOMS);
                    listing.bathrooms = property.optInt(BATHROOMS);

                    listing.balcony   = property.optInt(BALCONY);
                    listing.id = listingJson.optLong(ID);

                    ApiIntLabel propertyType = masterDataCache.getBuyPropertyType(property.optInt(UNIT_TYPE_ID));
                    listing.propertyType = null != propertyType ? propertyType.name : null;

                    ApiIntLabel propertyStatus1 = masterDataCache.getPropertyStatus(listingJson.optInt(CONS_STATUS_ID));
                    listing.propertyStatus = null != propertyStatus1 ? propertyStatus1.name : null;
                    listing.listingCategory = listingJson.optString(LISTING_CATEGORY);

                    listing.size = property.optDouble(SIZE);
                    listing.measure = property.optString(MEASURE);
                    if(listing.size != null && listing.measure != null) {
                        if ((listing.size == Math.floor(listing.size)) && !Double.isInfinite(listing.size)) {
                            listing.sizeInfo = listing.size > 0 ? String.valueOf(StringUtil.getFormattedNumber(listing.size.intValue())).concat(" ").concat(listing.measure) : null;
                        } else {
                            listing.sizeInfo = listing.size > 0 ? StringUtil.getFormattedNumber(listing.size).concat(" ").concat(listing.measure) : null;
                        }
                    }


                    listing.studyRoom = property.optInt(STUDY_ROOM);
                    listing.servantRoom = property.optInt(SERVANT_ROOM);
                    listing.poojaRoom = property.optInt(POOJA_ROOM);

                    buildBHKInfo(listing, property);

                    listing.latitude = listingJson.optDouble(LISTING_LATITUDE);
                    listing.longitude = listingJson.optDouble(LISTING_LONGITUDE);

                    if(listing.latitude == null || listing.longitude == null
                            || listing.latitude == 0 || listing.longitude == 0
                            || Double.isNaN(listing.latitude) || Double.isNaN(listing.longitude)) {
                        listing.latitude = listingJson.optDouble(LATITUDE);
                        listing.longitude = listingJson.optDouble(LONGITUDE);

                        if(listing.latitude == null || listing.longitude == null
                                || listing.latitude == 0 || listing.longitude == 0
                                || Double.isNaN(listing.latitude) || Double.isNaN(listing.longitude)) {

                            listing.latitude = project.optDouble(LATITUDE);
                            listing.longitude = project.optDouble(LONGITUDE);

                            if(listing.latitude == null || listing.longitude == null
                                    || listing.latitude == 0 || listing.longitude == 0
                                    || Double.isNaN(listing.latitude) || Double.isNaN(listing.longitude)) {

                                listing.latitude = listingJson.optDouble(LISTING_LATITUDE);
                                listing.longitude = listingJson.optDouble(LISTING_LONGITUDE);
                            }
                        }
                    }


                    listing.pricePerUnitArea = currentListingPrice.optInt(PRICE_PER_UNIT_AREA);
                    listing.price = currentListingPrice.optDouble(PRICE);
                    listing.localityAvgPrice = listingJson.optDouble(LOCALITY_AVG_PRICE);

                    listing.relativeCreateDate = listingJson.optString(CREATED_AT) != null ? getElapsedDaysFromNow(listingJson.optString(CREATED_AT)).toString().concat(" days ago") : null;

                    listing.isReadyToMove = isReadyToMove(listingJson.optInt(CONS_STATUS_ID));
                    String possessionDateStr = listingJson.optString(POSSESSION_DATE);
                    listing.propertyAge = !StringUtil.isBlank(possessionDateStr) ? getElapsedDaysFromNow(possessionDateStr).toString().concat(DAYS) : null;
                    listing.possessionDate = !StringUtil.isBlank(possessionDateStr) ? getMMMYYYYDateStringFromEpoch(possessionDateStr) : null;

                    Long age = listingJson.optLong(MIN_CONST_COMPLETION_DATE);
                    listing.age =  age > 0 ? getElapsedYearsFromNow(age) : 0;
                    //listing.maxConstructionCompletionDate =  listingJson.optLong(MAX_CONST_COMPLETION_DATE);

                    listing.noOfOpenSides = listingJson.optInt(NO_OPEN_SIDES);
                    listing.securityDeposit = listingJson.optInt(SECURITY_DEPOSIT);

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
                    listing.project.activeStatus = project.optString(ACTIVE_STATUS);
                    //listing.project.fullName = project.optString(NAME);
                    listing.project.url = project.optString(URL);
                    if (null != builder) {
                        listing.project.builderName = builder.optString(NAME);
                        //listing.project.fullName = builder.optString(NAME) + ' ' + project.optString(NAME);
                    }
                    // }

                    listing.localityId = locality.optLong(LOCALITY_ID);
                    listing.localityName = locality.optString(LABEL);
                    listing.suburbName = suburb.optString(LABEL);
                    listing.cityName = city.optString(LABEL);
                    listing.cityId = locality.optLong(CITY_ID);


                    listing.lisitingPostedBy = new LisitingPostedBy();

                    listing.lisitingPostedBy.type = null != sellerCompany ?
                            (sellerCompany.optString(TYPE) != null ? sellerCompany.optString(TYPE) : null) : null;
                    if (null != listing.lisitingPostedBy.type) {

                        if (listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_BROKER)
                                || listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_OWNER)
                                || listing.lisitingPostedBy.type.equalsIgnoreCase(POSTED_BY_BUILDER)) {
                            listing.lisitingPostedBy.name = sellerCompany.optString(NAME);
                            listing.lisitingPostedBy.id = sellerCompany.optLong(ID);
                            listing.lisitingPostedBy.userId = user.optLong(ID);
                            listing.lisitingPostedBy.logo = sellerCompany.optString(LOGO);
                            Double score = sellerCompany.optDouble(COMPANY_SCORE);
                            if(Double.isNaN(score)) {
                                listing.lisitingPostedBy.rating = null;
                            } else {
                                listing.lisitingPostedBy.rating = score / 2.0; // devided by 2 to show rating out of 5
                            }
                            listing.lisitingPostedBy.assist = sellerCompany.optBoolean(ASSIST);
                        }
                    }
                    listing.lisitingPostedBy.profilePictureURL = null != user ?
                            (user.optString(PROFILE_PICTURE_URL) != null ? user.optString(PROFILE_PICTURE_URL) : null) : null;
                    if(listing.lisitingPostedBy.name == null) {
                        listing.lisitingPostedBy.name = null != user ?
                                (user.optString(FULL_NAME) != null ? user.optString(FULL_NAME) : null) : null;
                    }

                    if (null != contactNumbers) {

                        for (int k = 0; k < contactNumbers.length(); k++) {
                            JSONObject contactNumbersJSONObject = contactNumbers.getJSONObject(k);
                            listing.lisitingPostedBy.number = contactNumbersJSONObject.getString("contactNumber");
                        }
                    }


                    listing.hasOffer = listingJson.optString(IS_OFFERED) != null && listingJson.optBoolean(IS_OFFERED);
                    listing.mainImageUrl = listingJson.optString(MAIN_IMAGE_URL);
                    listing.imageCount = listingJson.optInt(IMAGE_COUNT);
                    listing.sellerId = listingJson.optLong(SELLER_ID);

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

                    listing.floor = listingJson.optInt(FLOOR);
                    listing.totalFloors = listingJson.optInt(TOTAL_FLOORS);

                    if (null != listingJson.optString(FACING_ID)) {
                        //listing.facing = apiListing.facing.direction;   //TODO: get direction and translate facing id
                    }

                    listing.furnished = masterDataCache.translateApiLabel(listingJson.optString(FURNISHED));


                    if (null != listingJson.optString(LANDMARK_DISTANCE)) {
                        listing.landMarkDistance = Math.round(listingJson.optDouble(LANDMARK_DISTANCE) * 10) / 10;
                    }

                }
            }

        } catch (JSONException e) {
            Crashlytics.logException(e);
            CommonUtil.TLog(TAG, "Unable to parse listing data", e);
        }

        return listing;
    }

    private void buildBHKInfo(Listing listing, JSONObject property) {
        String unitTypeString = property.optString(UNIT_TYPE);

        if(unitTypeString != null && "plot".equalsIgnoreCase(unitTypeString)) {
            listing.bhkInfo = "residential plot";
        } else {

            listing.bhkInfo = listing.bedrooms > 0 ? listing.bedrooms.toString().concat(BHK_STR) : null;
            listing.bhkInfo = listing.bhkInfo != null ? listing.bhkInfo.concat(unitTypeString) : null;
        }


    }

}
