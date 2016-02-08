package com.makaan.event.agents.callback;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.network.JSONGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.agents.TopAgent;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.makaan.constants.ResponseConstants.DATA;

/**
 * Created by vaibhav on 20/01/16.
 */
public abstract class TopAgentsCallback extends JSONGetCallback {

    public static final String TAG = TopAgentsCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject dataResponse) {


        Type topAgentListType = new TypeToken<ArrayList<TopAgent>>() {
        }.getType();

        try {
            ArrayList<TopAgent> topAgentArrayList = MakaanBuyerApplication.gson.fromJson(dataResponse.getJSONArray(DATA).toString(), topAgentListType);
            onTopAgentsRcvd(topAgentArrayList);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing top agents data", e);
        }

    }

    @Override
    public void onError(ResponseError error) {
        //TODO handle error here
    }

    public abstract void onTopAgentsRcvd(ArrayList<TopAgent> topAgents);


}
