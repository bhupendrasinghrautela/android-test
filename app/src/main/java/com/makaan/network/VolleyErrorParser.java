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
    public static final String GENERIC_ERROR = "oops! something went wrong. let's get you back on the joyride";
    private static final String NETWORK_ERROR = "oops! looks like a connection issue. let's get you back on the joyride";
    private static final String SERVER_ERROR = "oops! something went wrong. let's get you back on the joyride";
    private static final String SERVICE_UNAVAILABLE_ERROR = "oops! something went wrong. let's get you back on the joyride";
    private static final String NO_CONTENT_ERROR = "no matching results!";
    private static final String NOT_FOUND_ERROR = "such a kill joy!\nthe page you were looking for is sitting at home.\nto find joy, find home";
    private static final String WEAK_NETWORK_ERROR = "oops! looks like a connection issue. let's get you back on the joyride";
    // todo
    private static final String AUTH_ERROR = "authentication error!";

    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @return
     */
    public static String getMessage(Object error) {
        if(null == error) {
            return GENERIC_ERROR;
        }
        if (error instanceof TimeoutError) {
            return WEAK_NETWORK_ERROR;

        } else if (error instanceof ServerError) {
            if(((VolleyError)error).networkResponse != null) {
                if(((VolleyError)error).networkResponse.statusCode == 503) {
                    return SERVICE_UNAVAILABLE_ERROR;
                }else if(((VolleyError)error).networkResponse.statusCode == 400){
                    return getErrorMessage(error);
                }
            }
            return SERVER_ERROR;

        } else if(isNoInternetConnection(error)) {
            return NETWORK_ERROR;

        } else if (isNetworkProblem(error)) {
            return WEAK_NETWORK_ERROR;

        } else if (error instanceof AuthFailureError) {
            return getAuthErrorMessage(error);

        } else if(error instanceof VolleyError) {
            NetworkResponse response = ((VolleyError)error).networkResponse;
            if(response != null) {
                if(response.statusCode == 204) {
                    return NO_CONTENT_ERROR;
                } else if(response.statusCode == 404) {
                    return NOT_FOUND_ERROR;
                }
            }
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

    private static String getAuthErrorMessage(Object err) {
        VolleyError error = (VolleyError) err;
        NetworkResponse response = error.networkResponse;

        if (response != null && response.data!=null && response.data.length>0) {
            BaseResponse baseResponse = (BaseResponse) JsonParser.parseJson(new String(response.data), BaseResponse.class);
            if(baseResponse!=null && baseResponse.getError()!=null &&
                    !TextUtils.isEmpty(baseResponse.getError().msg)){

                return baseResponse.getError().msg;
            }else{
                return AUTH_ERROR;
            }
        }else{
            return AUTH_ERROR;
        }
    }

}