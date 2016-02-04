package com.makaan.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonParser;
import com.makaan.request.upload.ImageUploadDto;
import com.makaan.response.document.ImageUploadResponse;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rohitgarg on 2/4/16.
 */

public class ImageMultipartRequest<T> extends Request<T> {

    private static final String FILE_PART_NAME = "image";
    private static final String WATER_MARK = "addWaterMark";
    private static final String DOMAIN_ID = "domainId";
    private static final String SOURCE_DOMAIN = "sourceDomain";
    private HttpEntity mHttpEntity;
    private final Response.Listener<String> mListener;
    private final ErrorListener mErrorListener;
    protected Map<String, String> headers;

    public ImageMultipartRequest(String url,
                                 ErrorListener errorListener,
                                 Listener<String> listener,
                                 ImageUploadDto imageUploadDto) {

        super(Method.POST, url, errorListener);

        mListener = listener;
        mErrorListener = errorListener;
        mHttpEntity = buildMultipartEntity(imageUploadDto);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("enctype", "multipart/form-data");

        return headers;
    }

    private HttpEntity buildMultipartEntity(ImageUploadDto imageUploadDto) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        FileBody fileBody = new FileBody(imageUploadDto.getImageFile());
        builder.addPart(FILE_PART_NAME, fileBody);
        builder.addTextBody(WATER_MARK, "false");
        builder.addTextBody(DOMAIN_ID, "1");
        builder.addTextBody(SOURCE_DOMAIN, "Makaan");
        for (Map.Entry<String, String> entry : imageUploadDto.getParams().entrySet()) {
            builder.addTextBody(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        String contentTypeHeader = mHttpEntity.getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException building the multipart request.");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        T result = null;
        return (Response<T>) Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        String str;
        NetworkResponse networkResponse = (NetworkResponse) response;
        try {
            str = new String(networkResponse.data, "UTF-8");
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse = (ImageUploadResponse) JsonParser.parseJson(str, ImageUploadResponse.class);
            mListener.onResponse(str);
            CommonUtil.TLog(str);
/*            if(imageUploadResponse.getStatusCode().equals("2XX")) {
                mListener.onResponse(imageUploadResponse.getData().getId().toString());
            }
            else{
                mErrorListener.onErrorResponse(new VolleyError());
            }*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}