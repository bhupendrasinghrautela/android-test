package com.makaan.network;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ResponseConstants;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaibhav on 23/12/15.
 */
public class MakaanNetworkClient {

    public static final String TAG = MakaanNetworkClient.class.getSimpleName();


    private RequestQueue makaanGetRequestQueue;
    private Gson gson;

    private AssetManager assetManager;
    private static MakaanNetworkClient instance;

    private MakaanNetworkClient(Context appContext) {
        makaanGetRequestQueue = Volley.newRequestQueue(appContext);
        gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();
        assetManager = appContext.getAssets();
    }

    public static void init(Context appContext) {
        instance = new MakaanNetworkClient(appContext);
    }


    public static MakaanNetworkClient getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void get(String url, final JSONGetCallback jsonGetCallback) {

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            response = response.getJSONObject(ResponseConstants.DATA);
                            //Object objResponse = gson.fromJson(response.toString(), jsonGetCallback.getResponseClass());
                            jsonGetCallback.onSuccess(response);

                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jsonGetCallback.onError();
                        Log.e(TAG, "Network error", error);
                    }
                });

        makaanGetRequestQueue.add(jsonRequest);
    }

    public void get(String url, final Type type, final ObjectGetCallback objectGetCallback, String... mockFile) {

        if (null != mockFile) {
            BufferedReader br = null;
            StringBuilder json = new StringBuilder();
            try {

                br = new BufferedReader(new InputStreamReader(assetManager.open(mockFile[0])));
                String line = null;
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONObject response = jsonObject.getJSONObject(ResponseConstants.DATA);

                Object objResponse = gson.fromJson(response.toString(), type);
                objectGetCallback.onSuccess(objResponse);

                objectGetCallback.onSuccess(objResponse);

            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        } else {

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response = response.getJSONObject(ResponseConstants.DATA);

                                Object objResponse = gson.fromJson(response.toString(), type);
                                objectGetCallback.onSuccess(objResponse);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSONException", e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            objectGetCallback.onError();
                            Log.e(TAG, "Network error", error);
                        }
                    });

            makaanGetRequestQueue.add(jsonRequest);
        }
    }

}
