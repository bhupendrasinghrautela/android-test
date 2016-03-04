package com.makaan.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.makaan.jarvis.JarvisClient;
import com.makaan.util.NetworkUtil;

/**
 * Created by sunil on 04/03/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (NetworkUtil.isNetworkAvailable(context)) {
            JarvisClient.getInstance().refreshJarvisSocket();
        }

    }
}
