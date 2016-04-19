package com.makaan.response.project;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SellerCard;
import com.makaan.response.ResponseError;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.user.Company;
import com.makaan.util.AppBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by vaibhav on 24/01/16.
 */
public class ProjectConfigCallback extends JSONGetCallback {


    private static final String TAG = ProjectConfigCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {

        SortedMap<Double,ProjectConfigItem> buyProjectConfigItems = new TreeMap<>();
        ArrayList<ProjectConfigItem> rentProjectConfigItems = new ArrayList<>();

        try {
            JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);

            JSONObject sellerPropertyCount = data.getJSONObject("sellerPropertyCount");
            Type type = new TypeToken<HashMap<Long, Long>>() {
            }.getType();
            HashMap<Long, Long> sellerPropCountMap = MakaanBuyerApplication.gson.fromJson(sellerPropertyCount.toString(), type);


            JSONObject configuration = data.getJSONObject("configuration");

            JSONObject resaleConfig = configuration.optJSONObject("Resale");
            JSONObject primaryConfig = configuration.optJSONObject("Primary");
            JSONObject rentalConfig = configuration.optJSONObject("Rental");

            ArrayList<ProjectConfigItem> temp = null;
            if (null != rentalConfig) {
                try {
                    temp = getProjectConfigItems(rentalConfig.getJSONObject("Apartment"), sellerPropCountMap);
                }
                catch (JSONException w){}
                if(temp!=null) {
                    rentProjectConfigItems.addAll(temp);
                    temp.clear();
                }
            }


            if (null != resaleConfig) {
                try {
                    temp = getProjectConfigItems(resaleConfig.getJSONObject("Apartment"), sellerPropCountMap);

                }
                catch (JSONException e){}
                if(temp!=null) {
                    for (ProjectConfigItem projectConfigItem : temp) {
                        buyProjectConfigItems.put(projectConfigItem.minPrice, projectConfigItem);
                    }
                }
            }

            if (null != primaryConfig) {
                try {
                    temp = getProjectConfigItems(primaryConfig.getJSONObject("Apartment"), sellerPropCountMap);

                }catch (JSONException e){}
                if(temp!=null) {
                    for (ProjectConfigItem projectConfigItem : temp) {
                        ProjectConfigItem getProjectConfigItem = buyProjectConfigItems.get(projectConfigItem.minPrice);
                        if (getProjectConfigItem == null) {
                            buyProjectConfigItems.put(projectConfigItem.minPrice, projectConfigItem);
                        } else {
                            getProjectConfigItem.propertyCount += projectConfigItem.propertyCount;
                            getProjectConfigItem.bedrooms.addAll(projectConfigItem.bedrooms);
                            if(getProjectConfigItem.maxPrice>projectConfigItem.maxPrice){
                                getProjectConfigItem.maxPrice = projectConfigItem.maxPrice;
                            }
                            for (SellerCard sellerCard : projectConfigItem.companies.values()) {
                                getProjectConfigItem.companies.put(sellerCard.sellerId, sellerCard);
                            }
                            buyProjectConfigItems.put(getProjectConfigItem.minPrice, getProjectConfigItem);
                        }
                    }
                }
            }

            AppBus.getInstance().post(new ProjectConfigEvent(new ArrayList<>(buyProjectConfigItems.values()), rentProjectConfigItems));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing project config", e);
        }
    }

    private ArrayList<ProjectConfigItem> getProjectConfigItems(JSONObject apartment, HashMap<Long, Long> sellerPropCountMap) {
        ArrayList<ProjectConfigItem> projectConfigItems = new ArrayList<>();
        boolean once = false;

        Type type = new TypeToken<SortedMap<Double, ArrayList<ListingDetail>>>() {
        }.getType();
        SortedMap<Double, ArrayList<ListingDetail>> apartMentListing = MakaanBuyerApplication.gson.fromJson(apartment.toString(), type);

        Double minPrice = null;
        ProjectConfigItem lastConfigItem = null;
        int count  = 0;
        for (Map.Entry<Double, ArrayList<ListingDetail>> entry : apartMentListing.entrySet()) {
            minPrice = entry.getKey();
            Double price = entry.getKey();
            ArrayList<ListingDetail> listings = entry.getValue();
            ProjectConfigItem projectConfigItem = new ProjectConfigItem();
            if (null != listings && listings.size() > 0) {

                for (ListingDetail listingDetail : listings) {
                    projectConfigItem.bedrooms.add(listingDetail.property.bedrooms);
                    projectConfigItem.propertyCount++;
                    Company company = listingDetail.companySeller.company;
                    SellerCard sellerCard = projectConfigItem.companies.get(company.id);

                    if (null == sellerCard) {
                        sellerCard = new SellerCard();
                        projectConfigItem.companies.put(company.id, sellerCard);
                        sellerCard.sellerId = company.id;
                        //sellerCard.assist =
                        sellerCard.name = company.name;
                        sellerCard.type = company.type;
                        if(company.score!=null) {
                            sellerCard.rating = company.score;
                        }
                        if(listingDetail.companySeller!=null && listingDetail.companySeller.user!=null
                                && listingDetail.companySeller.user.id!=null){
                            sellerCard.userId=listingDetail.companySeller.user.id;
                        }
                        if(listingDetail.companySeller.user!=null){
                            if(listingDetail.companySeller.user.contactNumbers!=null && listingDetail.companySeller.user.contactNumbers.size()>0) {
                                sellerCard.contactNo = listingDetail.companySeller.user.contactNumbers.get(0).contactNumber;
                            }
                        }
                        else{
                            sellerCard.rating = 0d;
                        }
                        sellerCard.noOfProperties = sellerPropCountMap.get(company.id);
                        if(projectConfigItem.topSellerCard == null){
                            projectConfigItem.topSellerCard = sellerCard;
                        } else {
                            if(projectConfigItem.topSellerCard.noOfProperties != null && sellerCard.noOfProperties != null) {
                                if (projectConfigItem.topSellerCard.noOfProperties < sellerCard.noOfProperties) {
                                    projectConfigItem.topSellerCard = sellerCard;
                                } else if (projectConfigItem.topSellerCard.noOfProperties.equals(sellerCard.noOfProperties)) {
                                    if(projectConfigItem.topSellerCard.rating != null && sellerCard.rating != null
                                            && projectConfigItem.topSellerCard.rating < sellerCard.rating) {
                                        projectConfigItem.topSellerCard = sellerCard;
                                    }
                                }
                            }
                        }
                    }

                }

            }
            if(projectConfigItems.size()==0) {
                projectConfigItems.add(projectConfigItem);
                projectConfigItem.minPrice = minPrice;
                lastConfigItem = projectConfigItem;
                if(projectConfigItem.propertyCount>0){
                    once = true;
                }
            }
            else if(projectConfigItem.propertyCount == 0 && once){
                if(lastConfigItem!=null) {
                    lastConfigItem.maxPrice = minPrice;
                }
                projectConfigItems.add(projectConfigItem);
                projectConfigItem.minPrice = minPrice;
                lastConfigItem = projectConfigItem;
            }
            else if(projectConfigItem.propertyCount>0){
                if(lastConfigItem!=null) {
                    lastConfigItem.maxPrice = minPrice;
                }
                projectConfigItems.add(projectConfigItem);
                projectConfigItem.minPrice = minPrice;
                lastConfigItem = projectConfigItem;
                once = true;
            }
        }

        return projectConfigItems;
    }

    @Override
    public void onError(ResponseError error) {
        ProjectConfigEvent projectConfigEvent = new ProjectConfigEvent();
        projectConfigEvent.error = error;
        AppBus.getInstance().post(projectConfigEvent);
    }
}
