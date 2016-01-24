package com.makaan.response.project;

import com.makaan.constants.ResponseConstants;
import com.makaan.network.JSONGetCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vaibhav on 24/01/16.
 */
public class ProjectConfigCallback extends JSONGetCallback {


    private static final String TAG = ProjectConfigCallback.class.getSimpleName();

    @Override
    public void onSuccess(JSONObject responseObject) {

        try {
            JSONObject data = responseObject.getJSONObject(ResponseConstants.DATA);
        } catch(JSONException e){
            
        }
    }
}
