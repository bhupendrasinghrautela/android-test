package com.makaan.jarvis;

import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OutgoingMessageEvent;
import com.makaan.jarvis.message.ChatMessages;
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
    private static ChatMessages mChatMessages;

    public static JarvisClient getInstance(){
        if(null== mInstance) {
            mInstance = new JarvisClient();
        }

        return mInstance;
    }

    public void destroy(){
        jarvisSocket.close();
    }

    public ChatMessages getChatMessages(){
        if(null == mChatMessages){
            mChatMessages = new ChatMessages();
        }

        return mChatMessages;
    }

    private JarvisClient(){
        eventBus = AppBus.getInstance();
        eventBus.register(this);
        jarvisSocket = new JarvisSocket();
        jarvisSocket.open();
    }

    @Subscribe
    public void onOutgoingMessage(OutgoingMessageEvent event){
        refreshJarvisSocket();
        jarvisSocket.sendMessage(event.message);
    }

    public void rateAgent(float rating){
        jarvisSocket.rateAgent(rating);
    }

    public void refreshJarvisSocket(){
        jarvisSocket.refresh();
    }

}
