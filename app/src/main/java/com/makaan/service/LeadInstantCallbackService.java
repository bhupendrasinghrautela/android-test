package com.makaan.service;

import com.makaan.event.lead.LeadInstantCallback;
import com.makaan.network.MakaanNetworkClient;

import org.json.JSONException;
import org.json.JSONObject;

import static com.makaan.constants.ApiConstants.*;
/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadInstantCallbackService implements MakaanService {

    private String TAG= LeadInstantCallbackService.class.getSimpleName();

    public void makeInstantCallbackRequest(String buyerNumber , String sellerNumber , int countryId , String jsonDump){

        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("fromNo",buyerNumber);
            mJsonObject.put("clientCountryId",countryId);
            mJsonObject.put("toNo",sellerNumber);
            mJsonObject.put("jsonDump",jsonDump);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       MakaanNetworkClient.getInstance().post(LEAD_INSTANT_CALLBACK_URL, mJsonObject ,new LeadInstantCallback(),TAG);
    }
}
