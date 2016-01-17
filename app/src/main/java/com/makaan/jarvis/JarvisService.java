package com.makaan.jarvis;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.makaan.util.AppBus;
import com.squareup.otto.Bus;

/**
 * Created by sunil on 11/01/16.
 */
public class JarvisService extends Service {


    private static JarvisSocket jarvisSocket;
    private static Bus eventBus;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        jarvisSocket = new JarvisSocket();
        jarvisSocket.open();

        eventBus = AppBus.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        jarvisSocket.close();
    }




}
