package com.makaan.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by rohitgarg on 4/29/16.
 */
public class StringRequest extends Request<String> {
    private static final String PROTOCOL_CHARSET = "utf-8";
    private final Response.Listener<String> mListener;
    private final String mRequestBody;

    public StringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mRequestBody = null;
    }

    public StringRequest(int method, String url, Response.Listener<String> listener, JSONObject jsonRequest, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mRequestBody = jsonRequest == null ? null : jsonRequest.toString();
    }

    public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(0, url, listener, errorListener);
    }

    protected void deliverResponse(String response) {
        this.mListener.onResponse(response);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(response.data);
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        } catch (OutOfMemoryError var5) {
            return Response.error(new ServerError());
        }
    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException var2) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", new Object[]{this.mRequestBody, "utf-8"});
            return null;
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }
}
