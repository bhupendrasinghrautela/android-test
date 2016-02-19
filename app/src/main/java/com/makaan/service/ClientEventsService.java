package com.makaan.service;

import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.constants.ApiConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.event.buyerjourney.ClientEventsByGetEvent;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.ResponseError;
import com.makaan.util.AppBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class ClientEventsService implements MakaanService {
    public void getClientEvents(int rows) {
        String url = ApiConstants.SITE_VISIT_CLIENT_EVENTS.concat("?sort=-performTime");
        if(rows > 0) {
            url = url.concat("&rows=1");
        }
        MakaanNetworkClient.getInstance().get(url, new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                if(responseObject != null) {
                    try {
                        JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
                        Type clientLeadsType = new TypeToken<ClientEventsByGetEvent>() {}.getType();
                        ClientEventsByGetEvent clientLeadsByGetEvent = MakaanBuyerApplication.gson.fromJson(data.toString(), clientLeadsType);
                        AppBus.getInstance().post(clientLeadsByGetEvent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(ResponseError error) {
                ClientEventsByGetEvent event = new ClientEventsByGetEvent();
                event.error = error;
                AppBus.getInstance().post(event);
            }
        });
    }

    public static Uri buildNavigationIntentUri(double lat, double lng) {
        Uri navUri;
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(ApiConstants.GOOGLE_NAV_BASE_STRING);
        sBuilder.append(String.valueOf(lat));
        sBuilder.append(",");
        sBuilder.append(String.valueOf(lng));

        navUri = Uri.parse(sBuilder.toString());
        return navUri;
    }
}
