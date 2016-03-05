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
    private static final String SERVER_ERROR = "the page you were looking for is chilling at server\\'s home. let\\'s refresh";
    private static final String SERVICE_UNAVAILABLE_ERROR = "makaan is overwhelmed with active users.\\ntoo many happy users! please try again in a minute";
    private static final String NO_CONTENT_ERROR = "found no results matching your search criteria";
    private static final String NOT_FOUND_ERROR = "such a kill joy!\\nthe page you were looking for is sitting at home";
    // TODO check below error
    private static final String WEAK_NETWORK_ERROR = "connection trouble! seems, we are not connected by the internet";

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
            if(((VolleyError)error).networkResponse != null && ((VolleyError)error).networkResponse.statusCode == 503) {
                return SERVICE_UNAVAILABLE_ERROR;
            }
            return SERVER_ERROR;
        } else if(isNoInternetConnection(error)) {
            return NETWORK_ERROR;
        } else if (isNetworkProblem(error)) {
            return WEAK_NETWORK_ERROR;
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

}