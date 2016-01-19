package com.makaan.jarvis;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sunil on 11/01/16.
 */
public class JarvisServiceCreator {

    public static void create(Context appContext){

        Intent serviceIntent = new Intent(appContext,JarvisService.class);
        appContext.startService(serviceIntent);
    }
}
