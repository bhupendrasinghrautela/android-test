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

    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */
    public static String getMessage(Object error, Context context) {
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.generic_error);
        } else if(isNoInternetConnection(error)) {
            return context.getResources().getString(R.string.no_network_connection);
        } else if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.weak_network_connection);
        }

        try {
            return getErrorMessage(error, context);
        }catch(Exception e){
            return context.getResources().getString(R.string.generic_error);
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
     * @param context
     * @return
     */
    private static String getErrorMessage(Object err, Context context) {
        VolleyError error = (VolleyError) err;
        NetworkResponse response = error.networkResponse;

        if (response != null && response.data!=null && response.data.length>0) {
            BaseResponse baseModel = (BaseResponse) JsonParser.parseJson(new String(response.data), BaseResponse.class);
            if(baseModel!=null && baseModel.getError()!=null &&
                    !TextUtils.isEmpty(baseModel.getError().getMsg())){

                baseModel.getError().setStatusCode(baseModel.getStatusCode());
                return baseModel.getError().getMsg();
            }else{
                return context.getResources().getString(R.string.generic_error);
            }
        }else{
            return context.getResources().getString(R.string.generic_error);
        }
    }

}