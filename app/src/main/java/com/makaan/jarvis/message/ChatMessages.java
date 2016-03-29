package com.makaan.jarvis.message;

import android.text.TextUtils;

import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sunil on 21/01/16.
 */
public class ChatMessages extends ArrayList<Message> {

    private static Bus eventBus = AppBus.getInstance();
    private static Map<String, Integer> jarvisMessageTypeMap =
            MasterDataCache.getInstance().getJarvisMessageTypeMap();

    @Override
    public boolean add(Message message) {
        if(null==message){
            return false;
        }


        if(null!=message.messageType && MessageType.outText==message.messageType){
            return super.add(message);
        }

        if(TextUtils.isEmpty(message.filtered) || message.isAgentAvailableMessage) {
            message.messageType = MessageType.inText;
        }else{
            Integer type = jarvisMessageTypeMap.get(message.filtered);
            if(null==type){
                return false;
            }
            message.messageType = MessageType.fromInt(type);

        }

        boolean result = super.add(message);
        IncomingMessageEvent event = new IncomingMessageEvent();
        event.message = message;
        eventBus.post(event);

        return result;
    }

}
