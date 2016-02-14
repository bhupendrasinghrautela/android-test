package com.makaan.network;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.LruBitmapCache;
import com.makaan.constants.RequestConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.request.CustomRequest;
import com.makaan.response.ResponseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaibhav on 23/12/15.
 */
public class MakaanNetworkClient {

    public static final String TAG = MakaanNetworkClient.class.getSimpleName();

    private RequestQueue makaanGetRequestQueue;
    private ImageLoader mImageLoader;

    private AssetManager assetManager;
    private static MakaanNetworkClient instance;

    private HashMap<String, String> requestUrlToTag = new HashMap<>();

    private MakaanNetworkClient(Context appContext) {
        makaanGetRequestQueue = Volley.newRequestQueue(appContext);

        assetManager = appContext.getAssets();
    }

    public static void init(Context appContext) {
        instance = new MakaanNetworkClient(appContext);
    }


    public static MakaanNetworkClient getInstance() {
        return instance;
    }

    public void get(String url, final JSONGetCallback jsonGetCallback) {
        get(url, jsonGetCallback, null, null);
    }

    public void get(String url, final JSONGetCallback jsonGetCallback, String mockFile) {
        get(url, jsonGetCallback, mockFile, null);
    }

    public void get(String url, final Type type, final ObjectGetCallback objectGetCallback, String mockFile) {
        get(url, type, objectGetCallback, mockFile, null, false);
    }

    public void get(String url, final Type type, final ObjectGetCallback objectGetCallback, String mockFile,boolean isDataArr) {
        get(url, type, objectGetCallback, mockFile, null, isDataArr);
    }

    public void get(String url, final StringRequestCallback stringRequestCallback) {
        get(url, stringRequestCallback, null);
    }

    public void get(final String url, final Type type, final ObjectGetCallback objectGetCallback) {
        get(url, type, objectGetCallback, null, null, false);
    }

    public void get(final String url, final Type type, final ObjectGetCallback objectGetCallback, boolean isDataArr) {
        get(url, type, objectGetCallback, null, null, isDataArr);
    }

    @SuppressWarnings("unchecked")
    public void get(final String inputUrl, final JSONGetCallback jsonGetCallback, String mockFile, String tag) {

        if (null != mockFile) {

            try {
                JSONObject mockFileResponse = readFromMockFile(mockFile);
                //JSONObject response = mockFileResponse.getJSONObject(ResponseConstants.DATA);
                jsonGetCallback.onSuccess(mockFileResponse);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }

        } else {

            final String urlToHit = appendSourceDomain(inputUrl);
            final JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, urlToHit, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            completeRequestInQueue(urlToHit);
                            jsonGetCallback.onSuccess(response);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            completeRequestInQueue(urlToHit);
                            jsonGetCallback.onError(getResponseError(error));
                            Log.e(TAG, "Network error", error);
                        }
                    });
            addToRequestQueue(jsonRequest, tag);
        }

    }


    public void get(final String inputUrl, final Type type, final ObjectGetCallback objectGetCallback, String mockFile, String tag, final boolean isDataArr) {

        if (null != mockFile) {
            try {
                JSONObject mockFileResponse = readFromMockFile(mockFile);
                Object objResponse;
                if (isDataArr) {
                    JSONArray response = mockFileResponse.getJSONArray(ResponseConstants.DATA);
                    objResponse = MakaanBuyerApplication.gson.fromJson(response.toString(), type);
                } else {
                    JSONObject response = mockFileResponse.getJSONObject(ResponseConstants.DATA);
                    objResponse = MakaanBuyerApplication.gson.fromJson(response.toString(), type);
                }

                objectGetCallback.onSuccess(objResponse);

            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
        } else {

            final String urlToHit = appendSourceDomain(inputUrl);
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, urlToHit, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            completeRequestInQueue(urlToHit);
                            if (null != response) {
                                try {
                                    Object objResponse;
                                    if (isDataArr) {
                                        JSONArray tempResponse = response.getJSONArray(ResponseConstants.DATA);
                                        objResponse = MakaanBuyerApplication.gson.fromJson(tempResponse.toString(), type);
                                    } else {
                                        response = response.getJSONObject(ResponseConstants.DATA);
                                        objResponse = MakaanBuyerApplication.gson.fromJson(response.toString(), type);
                                    }


                                    objectGetCallback.onSuccess(objResponse);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSONException", e);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            completeRequestInQueue(urlToHit);
                            objectGetCallback.onError(getResponseError(error));
                            Log.e(TAG, "Network error", error);
                        }
                    });

            addToRequestQueue(jsonRequest, tag);

        }
    }


   /* public void addSyncGet(JsonObjectRequest jsonObjectRequest) {
        makaanGetRequestQueue.add(jsonObjectRequest);
    }*/

    public void get(final String inputUrl, final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(inputUrl);
        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onError(getResponseError(error));
                        Log.e(TAG, "Network error", error);
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void getSearch(final String inputUrl, final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(inputUrl);
        StringRequest stringRequest = new StringRequest
                (Request.Method.GET, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onError(getResponseError(error));
                        Log.e(TAG, "Network error", error);
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void loginRegisterPost(final String url, JSONObject jsonObject,
                                  final StringRequestCallback stringRequestCallback, String tag){
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("applicationType", "MAKAAN");
                return params;
            }
        };
        addToRequestQueue(stringRequest, tag);
    }

    public void post(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onSuccess(response);
                        }

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onError(getResponseError(error));
                        }
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void post(final String url, final Type type,JSONObject jsonObject,
                       final ObjectGetCallback objectGetCallback, String tag, final boolean isDataArr) {

        JsonObjectRequest stringRequest = new JsonObjectRequest
                (Request.Method.POST,url,jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        completeRequestInQueue(url);
                        if (null != response) {
                            try {
                                Object objResponse;
                                if (isDataArr) {
                                    JSONArray tempResponse = response.getJSONArray(ResponseConstants.DATA);
                                    objResponse = MakaanBuyerApplication.gson.fromJson(tempResponse.toString(), type);
                                } else {
                                    response = response.getJSONObject(ResponseConstants.DATA);
                                    objResponse = MakaanBuyerApplication.gson.fromJson(response.toString(), type);
                                }
                                objectGetCallback.onSuccess(objResponse);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSONException", e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        objectGetCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void loginPost(final String url, final Map<String, String> params,
                     final StringRequestCallback stringRequestCallback, String tag) {

        CustomRequest stringRequest = new CustomRequest
                (Request.Method.POST, url,params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(null!=error && null!=error.networkResponse) {
                            String errorString = new String(error.networkResponse.data);
                            Log.e("Error : ", errorString);
                        }
                        completeRequestInQueue(url);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                }){

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

        };
        addToRequestQueue(stringRequest, tag);
    }

    public void socialLoginPost(final String url, final StringRequestCallback stringRequestCallback,
                                String tag){

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void postTrack(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onSuccess(response);
                        }


                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onError(getResponseError(error));
                        }
                        Log.e("Analytics error : ", VolleyErrorParser.getMessage(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void delete(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        StringRequest stringRequest = new StringRequest
                (Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void delete(final String url, final Type type,
                       final ObjectGetCallback objectGetCallback, String tag, final boolean isDataArr) {

        JsonObjectRequest stringRequest = new JsonObjectRequest
                (Request.Method.DELETE, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        completeRequestInQueue(url);
                        if (null != response) {
                            try {
                                Object objResponse;
                                if (isDataArr) {
                                    JSONArray tempResponse = response.getJSONArray(ResponseConstants.DATA);
                                    objResponse = MakaanBuyerApplication.gson.fromJson(tempResponse.toString(), type);
                                } else {
                                    response = response.getJSONObject(ResponseConstants.DATA);
                                    objResponse = MakaanBuyerApplication.gson.fromJson(response.toString(), type);
                                }
                                objectGetCallback.onSuccess(objResponse);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSONException", e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        objectGetCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void put(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        StringRequest stringRequest = new StringRequest
                (Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(url);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }


    private void addToRequestQueue(Request req, String tag) {
        if (null == tag) {
            tag = requestUrlToTag.get(req.getUrl());
            if (null == tag) {
                tag = MakaanBuyerApplication.randomString.nextString();
                requestUrlToTag.put(req.getUrl(), tag);
            }
            req.setTag(tag);
        } else {
            req.setTag(tag);
        }

        cancelFromRequestQueue(req, tag);
        Log.e(TAG, "URL: -> " + req.getUrl());

        makaanGetRequestQueue.add(req);
    }


    private String appendSourceDomain(String url) {

        if (!url.contains(RequestConstants.SOURCE_DOMAIN_MAKAAN)) {
            if (url.contains("?")) {
                url = url.concat("&").concat(RequestConstants.SOURCE_DOMAIN_MAKAAN);
            } else {
                url = url.concat("?").concat(RequestConstants.SOURCE_DOMAIN_MAKAAN);
            }
        }

        return url;
    }

    private void cancelFromRequestQueue(Request req, String tag) {
        if (null != makaanGetRequestQueue) {
            if (tag == null && null != req) {
                tag = requestUrlToTag.get(req.getUrl());
                makaanGetRequestQueue.cancelAll(tag);
            } else if (null != tag) {
                makaanGetRequestQueue.cancelAll(tag);
            }
        }

    }

    private void completeRequestInQueue(String url) {
        requestUrlToTag.remove(url);
    }


    /**
     * Retrieves volley image loader
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.makaanGetRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    private JSONObject readFromMockFile(String mockFile) throws JSONException, IOException {
        BufferedReader br = null;
        StringBuilder json = new StringBuilder();


        br = new BufferedReader(new InputStreamReader(assetManager.open(mockFile)));
        String line = null;
        while ((line = br.readLine()) != null) {
            json.append(line);
        }
        return new JSONObject(json.toString());

    }

    private ResponseError getResponseError(VolleyError error){
        ResponseError responseError = new ResponseError();
        responseError.error = error;
        responseError.msg = VolleyErrorParser.getMessage(error);
        return responseError;
    }

}
