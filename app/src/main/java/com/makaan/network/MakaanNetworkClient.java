package com.makaan.network;


import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.LruBitmapCache;
import com.makaan.constants.RequestConstants;
import com.makaan.constants.ResponseConstants;
import com.makaan.request.CustomRequest;
import com.makaan.request.JsonObjectRequest;
import com.makaan.request.StringRequest;
import com.makaan.response.ResponseError;
import com.makaan.util.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    private final Context context;

    private CustomVolleyRequestQueue makaanGetRequestQueue;
    private ImageLoader mImageLoader;

    private AssetManager assetManager;
    private static MakaanNetworkClient instance;

    private HashMap<String, String> requestUrlToTag = new HashMap<>();

    private MakaanNetworkClient(Context appContext) {
        makaanGetRequestQueue = CustomVolleyRequestQueue.newRequestQueue(appContext);

        assetManager = appContext.getAssets();
        context = appContext;
    }

    public static void init(Context appContext) {
        if(instance == null) {
            synchronized (MakaanNetworkClient.class) {
                if(instance == null) {
                    instance = new MakaanNetworkClient(appContext);
                }
            }
        }
    }


    public static MakaanNetworkClient getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue(){
        return makaanGetRequestQueue;
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

            JSONObject diskFileFileResponse = readFromDiskFile(mockFile);
            if(diskFileFileResponse != null) {
                jsonGetCallback.onSuccess(diskFileFileResponse);
            } else {

                try {
                    JSONObject mockFileResponse = readFromMockFile(mockFile);
                    //JSONObject response = mockFileResponse.getJSONObject(ResponseConstants.DATA);
                    jsonGetCallback.onSuccess(mockFileResponse);

                    writeMockFileToDisk(mockFileResponse, mockFile);
                } catch (Exception e) {
                    Crashlytics.log(mockFile);
                    Crashlytics.logException(e);
                    CommonUtil.TLog(TAG, "Exception", e);
                }
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
                            CommonUtil.TLog(TAG, "Network error", error);
                        }
                    });
            addToRequestQueue(jsonRequest, tag);
        }

    }

    private void writeMockFileToDisk(JSONObject mockFileResponse, String mockFile) {
        if(context == null || mockFileResponse == null || TextUtils.isEmpty(mockFile)) {
            return;
        }
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(mockFile, Context.MODE_PRIVATE);
            outputStream.write(mockFileResponse.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {

            Crashlytics.log(mockFile);
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
        }
    }

    private JSONObject readFromDiskFile(String mockFile) {
        if(context == null) {
            return null;
        }
        File file = new File(context.getFilesDir(), mockFile);
        if(file.exists()) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                JSONArray response = null;
                try {
                    JSONObject diskFileResponse = new JSONObject(text.toString());
                    return diskFileResponse;
                } catch (JSONException e) {

                    Crashlytics.log(mockFile);
                    Crashlytics.logException(e);
                    CommonUtil.TLog("exception", e);
                }
            }
            catch (IOException e) {

                Crashlytics.log(mockFile);
                Crashlytics.logException(e);
            }
        }

        return null;
    }


   /* public void addSyncGet(JsonObjectRequest jsonObjectRequest) {
        makaanGetRequestQueue.add(jsonObjectRequest);
    }*/


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

                Crashlytics.log(mockFile);
                Crashlytics.logException(e);
                CommonUtil.TLog(TAG, "Exception", e);
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

                                } catch (JSONException | JsonSyntaxException | IllegalArgumentException e) {
//                                    Log.e(TAG, "JSONException", e);
                                    Crashlytics.log(urlToHit);
                                    Crashlytics.logException(e);
                                    objectGetCallback.onError(getResponseError(new VolleyError()));
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            completeRequestInQueue(urlToHit);
                            objectGetCallback.onError(getResponseError(error));
                            CommonUtil.TLog(TAG, "Network error", error);
                        }
                    });

            addToRequestQueue(jsonRequest, tag);

        }
    }

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
                        CommonUtil.TLog(TAG, "Network error", error);
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
                        CommonUtil.TLog(TAG, "Network error", error);
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void loginRegisterPost(final String url, JSONObject jsonObject,
                                  final StringRequestCallback stringRequestCallback, String tag){

        final String urlToHit = appendSourceDomain(url);

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
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

        final String urlToHit = appendSourceDomain(url);

        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onSuccess(response);
                        }

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onError(getResponseError(error));
                        }
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void postWithArray(final String url, JSONArray jsonArray,
                     final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(url);

        JsonObjectRequest stringRequest = new JsonObjectRequest
                (Request.Method.POST, jsonArray, urlToHit, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        completeRequestInQueue(urlToHit);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onSuccess(response.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        if(null!=stringRequestCallback) {
                            stringRequestCallback.onError(getResponseError(error));
                        }
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void post(final String url, final Type type,JSONObject jsonObject,
                       final ObjectGetCallback objectGetCallback, String tag, final boolean isDataArr) {

        final String urlToHit = appendSourceDomain(url);

        JsonObjectRequest stringRequest = new JsonObjectRequest
                (Request.Method.POST,urlToHit,jsonObject, new Response.Listener<JSONObject>() {
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

                            } catch (JSONException | JsonSyntaxException | IllegalArgumentException e) {
//                                Log.e(TAG, "JSONException", e);
                                Crashlytics.log(urlToHit);
                                Crashlytics.logException(e);
                                objectGetCallback.onError(getResponseError(new VolleyError()));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        objectGetCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void loginPost(final String url, final Map<String, String> params,
                     final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(url);

        CustomRequest stringRequest = new CustomRequest
                (Request.Method.POST, urlToHit,params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(null!=error && null!=error.networkResponse) {
                            String errorString = new String(error.networkResponse.data);
                            CommonUtil.TLog("Error : ", errorString);
                        }
                        completeRequestInQueue(urlToHit);
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

    public void postFormWithParams(final String urlToHit, final Map<String, String> params,
                                final StringRequestCallback stringRequestCallback,
                                String tag){

        CustomRequest stringRequest = new CustomRequest
                (Request.Method.POST, urlToHit,params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(null!=error && null!=error.networkResponse) {
                            String errorString = new String(error.networkResponse.data);
                            CommonUtil.TLog("Error : ", errorString);
                        }
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                }){

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

        };
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
                        CommonUtil.TLog("Analytics error : ", VolleyErrorParser.getMessage(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void delete(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(url);

        StringRequest stringRequest = new StringRequest
                (Request.Method.DELETE, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void delete(final String url, final Type type,
                       final ObjectGetCallback objectGetCallback, String tag, final boolean isDataArr) {

        final String urlToHit = appendSourceDomain(url);

        JsonObjectRequest stringRequest = new JsonObjectRequest
                (Request.Method.DELETE, urlToHit,null, new Response.Listener<JSONObject>() {
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

                                Crashlytics.log(urlToHit);
                                Crashlytics.logException(e);
                                CommonUtil.TLog(TAG, "JSONException", e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        objectGetCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }

    public void put(final String url, JSONObject jsonObject,
                     final StringRequestCallback stringRequestCallback, String tag) {

        final String urlToHit = appendSourceDomain(url);

        StringRequest stringRequest = new StringRequest
                (Request.Method.PUT, urlToHit, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onSuccess(response);

                    }
                }, jsonObject, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        completeRequestInQueue(urlToHit);
                        stringRequestCallback.onError(getResponseError(error));
                    }
                });
        addToRequestQueue(stringRequest, tag);
    }


    public void addToRequestQueue(Request req, String tag) {
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
        CommonUtil.TLog(TAG, "URL: -> " + req.getUrl());

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
            mImageLoader = new ImageLoader(this.makaanGetRequestQueue, new LruBitmapCache(12));
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
