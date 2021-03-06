package com.makaan.service;

import android.app.Activity;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.buyerjourney.PropertyRequirementsByGetEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.buyerjourney.AgentRating;
import com.makaan.response.buyerjourney.Company;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientLeadsService implements MakaanService {
    public final String TAG = ClientLeadsService.class.getSimpleName();

    public void requestClientLeads() {
        String detailsURL = ApiConstants.ICRM_CLIENT_LEADS;
        detailsURL = detailsURL.concat("?sort=-createdAt");
        MakaanNetworkClient.getInstance().get(detailsURL, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                        Type clientLeadsType = new TypeToken<ClientLeadsByGetEvent>() {}.getType();
                        ClientLeadsByGetEvent clientLeadsByGetEvent = MakaanBuyerApplication.gson.fromJson(data.toString(), clientLeadsType);
                        AppBus.getInstance().post(clientLeadsByGetEvent);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        CommonUtil.TLog("exception", e);
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                ClientLeadsByGetEvent event = new ClientLeadsByGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }

    public void requestpropertyRequirements(int rows) {
//        String detailsURL = ApiConstants.ICRM_CLIENT_LEADS.concat("?sort=-clientActivity.phaseId&fields=clientActivity.phaseId&rows=1");
        String detailsURL = ApiConstants.PROPERTY_REQUIREMENTS.concat("?fields=id");
        if(rows > 0) {
            detailsURL = detailsURL.concat("&rows=" + rows);
        }
        MakaanNetworkClient.getInstance().get(detailsURL, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                        Type propertyRequirementsType = new TypeToken<PropertyRequirementsByGetEvent>() {}.getType();
                        PropertyRequirementsByGetEvent propertyRequirementsByGetEvent = MakaanBuyerApplication.gson.fromJson(data.toString(), propertyRequirementsType);
                        AppBus.getInstance().post(propertyRequirementsByGetEvent);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        CommonUtil.TLog("exception", e);
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                PropertyRequirementsByGetEvent event = new PropertyRequirementsByGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }

    public void requestClientLeadsActivity() {
        String detailsURL = ApiConstants.ICRM_CLIENT_LEADS.concat("?sort=-clientActivity.phaseId&fields=clientActivity.phaseId&rows=1");
//        String detailsURL = ApiConstants.PROPERTY_REQUIREMENTS.concat("?fields=id&rows=1");
        MakaanNetworkClient.getInstance().get(detailsURL, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                        Type clientLeadsType = new TypeToken<ClientLeadsByGetEvent>() {}.getType();
                        ClientLeadsByGetEvent clientLeadsByGetEvent = MakaanBuyerApplication.gson.fromJson(data.toString(), clientLeadsType);
                        AppBus.getInstance().post(clientLeadsByGetEvent);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        CommonUtil.TLog("exception", e);
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                ClientLeadsByGetEvent event = new ClientLeadsByGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }

    public void requestClientLeadCompanies(ArrayList<Long> ids) {
        String detailsURL = ApiConstants.USER_SERVICE_ENTITY_COMPANIES;
        for(Long id : ids) {
            if(id != null) {
                detailsURL = detailsURL.concat(String.format(ApiConstants.USER_SERVICE_ENTITY_COMPANIES_FILTER, id.intValue()));
            }
        }
        if(ids.size() > 0 && detailsURL.length() > 0) {
            detailsURL = detailsURL.substring(0, detailsURL.length() - 1);
        }
        detailsURL = detailsURL.concat("&fields=name,id,score");

        MakaanNetworkClient.getInstance().get(detailsURL, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                if (responseObject != null) {
                    try {
                        JSONArray data = responseObject.getJSONArray(ResponseConstants.DATA);
                        Type companyType = new TypeToken<ArrayList<Company>>() {}.getType();
                        ArrayList<Company> companies = MakaanBuyerApplication.gson.fromJson(data.toString(), companyType);
                        AppBus.getInstance().post(companies);
                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        CommonUtil.TLog("exception", e);
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                ArrayList<Company> companies = new ArrayList<>();
                AppBus.getInstance().post(companies);
            }
        });
    }

    public void postSellerRating(JSONObject jsonObject, final Context context, final float ratingValue, final String comment, final Long sellerId) {
        Type type = new TypeToken<AgentRating>() {}.getType();
        MakaanNetworkClient.getInstance().post(ApiConstants.SELLER_RATING, type, jsonObject, new ObjectGetCallback() {

            @Override
            public void onSuccess(Object responseObject) {
                AgentRating rating = (AgentRating) responseObject;
                if(context != null && context instanceof Activity) {
                    if(!((Activity)context).isFinishing()) {
                        /*----------------------- track events-------------------------*/
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardSellerRating);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ratingValue, sellerId));
                        MakaanEventPayload.endBatch(context, MakaanTrackerConstants.Action.click);

                        Properties propertis = MakaanEventPayload.beginBatch();
                        propertis.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardSellerRating);
                        propertis.put(MakaanEventPayload.LABEL, String.format("%s_%s_%s", comment, "Feedback", sellerId));
                        MakaanEventPayload.endBatch(context, MakaanTrackerConstants.Action.click);
                        /*-----------------------------------------------------------------*/
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                if(context != null && context instanceof Activity) {
                    if(!((Activity)context).isFinishing()) {
                        /*----------------------- track events-------------------------*/
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardSellerRating);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s_%s", comment, "Feedback", sellerId));
                        MakaanEventPayload.endBatch(context, MakaanTrackerConstants.Action.click);
                        /*-----------------------------------------------------------------*/
                    }
                }
            }
        }, TAG, false);
    }
}
