package com.makaan.util;

import com.makaan.R;

/**
 * Created by rohitgarg on 3/4/16.
 */
public class ErrorUtil {
    public static final int STATUS_CODE_NO_NETWORK = -1;
    public static final int STATUS_CODE_GENERIC = -2;
    public static final int STATUS_CODE_NO_CONTENT = 204;

    public static int getErrorMessageId(int statusCode) {
        return getErrorMessageId(statusCode, false);
    }

    public static int getErrorMessageId(int statusCode, boolean isSerp) {
        switch (statusCode) {
            case STATUS_CODE_NO_CONTENT:
                if(isSerp) {
                    return R.string.no_content_serp_error;
                } else {
                    return R.string.no_content_error;
                }
            case 404:
                return R.string.not_found_error;
            case 500:
                return R.string.internal_server_error;
            case 503:
                return R.string.service_unavailable_error;
            case STATUS_CODE_NO_NETWORK:
                return R.string.no_connection_error;
            case STATUS_CODE_GENERIC:
            default:
                return R.string.generic_error;
        }
    }
}
