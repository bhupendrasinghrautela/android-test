package com.makaan.jarvis;

import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisClient {
    private Bus eventBus;

    public static void init(){
        new JarvisClient();
    }

    private JarvisClient(){
        eventBus = AppBus.getInstance();
    }


    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){

    }

}
