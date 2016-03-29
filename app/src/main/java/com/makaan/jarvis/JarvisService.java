package com.makaan.jarvis;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sunil on 11/01/16.
 */
public class JarvisService extends Service {


    private static JarvisClient mJarvisClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Create Jarvis client
        mJarvisClient = JarvisClient.getInstance();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mJarvisClient.destroy();
    }




}
