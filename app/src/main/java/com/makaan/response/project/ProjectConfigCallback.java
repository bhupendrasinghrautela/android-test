package com.makaan.response.project;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SellerCard;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.user.Company;
import com.makaan.util.AppBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 24/01/16.
 */
public class ProjectConfigCallback extends JSONGetCallback {


    private static final String TAG = ProjectConfigCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {

        ArrayList<ProjectConfigItem> buyProjectConfigItems = new ArrayList<>();
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

            ArrayList<ProjectConfigItem> temp;
            if (null != rentalConfig) {
                temp = getProjectConfigItems(rentalConfig.getJSONObject("Apartment"), sellerPropCountMap);
                rentProjectConfigItems.addAll(temp);
                temp.clear();
            }


            if (null != resaleConfig) {
                temp = getProjectConfigItems(resaleConfig.getJSONObject("Apartment"), sellerPropCountMap);
                buyProjectConfigItems.addAll(temp);
            }

            if (null != primaryConfig) {
                temp = getProjectConfigItems(primaryConfig.getJSONObject("Apartment"), sellerPropCountMap);
                buyProjectConfigItems.addAll(temp);
            }


            AppBus.getInstance().post(new ProjectConfigEvent(buyProjectConfigItems, rentProjectConfigItems));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing project config", e);
        }
    }

    private ArrayList<ProjectConfigItem> getProjectConfigItems(JSONObject apartment, HashMap<Long, Long> sellerPropCountMap) {
        ArrayList<ProjectConfigItem> projectConfigItems = new ArrayList<>();

        Type type = new TypeToken<HashMap<Double, ArrayList<ListingDetail>>>() {
        }.getType();
        HashMap<Double, ArrayList<ListingDetail>> apartMentListing = MakaanBuyerApplication.gson.fromJson(apartment.toString(), type);

        Double minPrice = null;
        for (Map.Entry<Double, ArrayList<ListingDetail>> entry : apartMentListing.entrySet()) {
            Double price = entry.getKey();
            ArrayList<ListingDetail> listings = entry.getValue();
            if (null != minPrice && minPrice != 0 && null != listings && listings.size() > 0) {
                ProjectConfigItem projectConfigItem = new ProjectConfigItem();
                projectConfigItems.add(projectConfigItem);

                projectConfigItem.minPrice = minPrice;
                projectConfigItem.maxPrice = price;

                for (ListingDetail listingDetail : listings) {
                    projectConfigItem.bedrooms.add(listingDetail.property.bedrooms);
                    projectConfigItem.propertyCount++;
                    projectConfigItem.sellerCount++;
                    Company company = listingDetail.companySeller.company;
                    SellerCard sellerCard = projectConfigItem.companies.get(company.id);

                    if (null == sellerCard) {
                        sellerCard = new SellerCard();
                        projectConfigItem.companies.put(company.id, sellerCard);
                        sellerCard.sellerId = company.id;
                        //sellerCard.assist =
                        sellerCard.name = company.name;
                        sellerCard.type = company.type;
                        sellerCard.noOfProperties = sellerPropCountMap.get(company.id);
                    }

                }

            }

            minPrice = price;
        }

        return projectConfigItems;
    }
}
