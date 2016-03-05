package com.makaan.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.makaan.R;
import com.makaan.response.BaseResponse;
import com.makaan.util.JsonParser;


/**
 * Created by sunil on 03/12/15.
 */
public class VolleyErrorParser {
    private static final String GENERIC_ERROR = "oops! something went wrong. please try again";
    private static final String NETWORK_ERROR = "connection trouble! seems, we are not connected by the internet";
    private static final String WEAK_NETWORK_ERROR = "your network connection seems to be weak, please check and try again.";

    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @return
     */
    public static String getMessage(Object error) {
        if(null==error){
            return GENERIC_ERROR;
        }
        if (error instanceof TimeoutError) {
            return GENERIC_ERROR;
        } else if(isNoInternetConnection(error)) {
            return NETWORK_ERROR;
        } else if (isNetworkProblem(error)) {
            return WEAK_NETWORK_ERROR;
        }

        try {
            return getErrorMessage(error);
        }catch(Exception e){
            return GENERIC_ERROR;
        }
    }

    /**
     * Determines whether the error is related to network
     * @param error
     * @return
     */
    public static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError);
    }

    public static boolean isNoInternetConnection(Object error) {
        return error instanceof NoConnectionError;
    }


    /**
     * Determines whether the error is related to server
     * @param error
     * @return
     */
    public static boolean isServerProblem(Object error) {
        return (error instanceof ServerError);
    }

    /**
     * Determines whether the error is related to server
     * @param error
     * @return
     */
    public static boolean isAuthProblem(Object error) {
        return (error instanceof AuthFailureError);
    }

    /**
     * Handles the error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     * @param err
     * @return
     */
    private static String getErrorMessage(Object err) {
        VolleyError error = (VolleyError) err;
        NetworkResponse response = error.networkResponse;

        if (response != null && response.data!=null && response.data.length>0) {
            BaseResponse baseResponse = (BaseResponse) JsonParser.parseJson(new String(response.data), BaseResponse.class);
            if(baseResponse!=null && baseResponse.getError()!=null &&
                    !TextUtils.isEmpty(baseResponse.getError().msg)){

                return baseResponse.getError().msg;
            }else{
                return GENERIC_ERROR;
            }
        }else{
            return GENERIC_ERROR;
        }
    }

}