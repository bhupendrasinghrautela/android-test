package com.makaan.service;

import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.makaan.event.lead.LeadInstantCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.makaan.constants.ApiConstants.*;
/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadInstantCallbackService implements MakaanService {

    private String TAG= LeadInstantCallbackService.class.getSimpleName();

    public void makeInstantCallbackRequest(String buyerNumber , String sellerNumber , int countryId , JSONObject jsonDump,
                                           Long userId ,int cityId, String listingCategory){

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("fromNo",buyerNumber);
            mJsonObject.put("clientCountryId",countryId);
            mJsonObject.put("toNo",sellerNumber);
            if(userId!=null && userId!=0) {
                mJsonObject.put("userId", userId);
            }
            if(cityId>0) {
                mJsonObject.put("cityId", cityId);
            }
            if(!TextUtils.isEmpty(listingCategory)) {
                mJsonObject.put("listingCategory", listingCategory);
            }
            mJsonObject.put("jsonDump", jsonDump.toString());
            CommonUtil.TLog("json ", "Dump " + jsonDump.toString());
        } catch (JSONException e) {
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
        }

       MakaanNetworkClient.getInstance().post(LEAD_INSTANT_CALLBACK_URL, mJsonObject ,new LeadInstantCallback(),TAG);
    }
}
