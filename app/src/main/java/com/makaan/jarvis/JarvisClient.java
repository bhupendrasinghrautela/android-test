package com.makaan.jarvis;

import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OutgoingMessageEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisClient {

    private static JarvisSocket jarvisSocket;
    private static Bus eventBus;
    private static JarvisClient mInstance;

    public static JarvisClient getInstance(){
        if(null== mInstance) {
            new JarvisClient();
        }

        return mInstance;
    }

    public void destroy(){
        jarvisSocket.close();
    }

    private JarvisClient(){
        eventBus = AppBus.getInstance();
        eventBus.register(this);
        jarvisSocket = new JarvisSocket();
        jarvisSocket.open();
    }

    @Subscribe
    public void onOutgoingMessage(OutgoingMessageEvent event){
        jarvisSocket.sendMessage(event.message);
    }

}