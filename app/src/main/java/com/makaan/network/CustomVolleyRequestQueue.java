package com.makaan.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.makaan.constants.ApiConstants;

import java.io.File;

/**
 * Created by rohitgarg on 3/9/16.
 */
public class CustomVolleyRequestQueue extends RequestQueue {
    public CustomVolleyRequestQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
        super(cache, network, threadPoolSize, delivery);
    }

    public CustomVolleyRequestQueue(Cache cache, Network network, int threadPoolSize) {
        super(cache, network, threadPoolSize);
    }

    public CustomVolleyRequestQueue(Cache cache, Network network) {
        super(cache, network);
    }

    public static CustomVolleyRequestQueue newRequestQueue(Context context) {
        HttpStack stack = null;
        File cacheDir = new File(context.getCacheDir(), "volley");
        String userAgent = "volley/0";

        try {
            String network = context.getPackageName();
            PackageInfo queue = context.getPackageManager().getPackageInfo(network, 0);
            userAgent = network + "/" + queue.versionCode;
        } catch (PackageManager.NameNotFoundException var6) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        BasicNetwork network1 = new BasicNetwork(stack);
        CustomVolleyRequestQueue queue1 = new CustomVolleyRequestQueue(new DiskBasedCache(cacheDir), network1);
        queue1.start();
        return queue1;
    }

    @Override
    public <T> Request<T> add(Request<T> request) {

        request.setRetryPolicy(new DefaultRetryPolicy(
                ApiConstants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return super.add(request);
    }

}
