package com.makaan.jarvis.message;

import android.text.TextUtils;

import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.util.AppBus;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 21/01/16.
 */
public class ChatMessages extends ArrayList<Message> {

    private static Bus eventBus = AppBus.getInstance();

    @Override
    public boolean add(Message message) {
        if(null==message){
            return false;
        }

        boolean result = super.add(message);

        if(TextUtils.isEmpty(message.filtered)) {
            message.messageType = MessageType.inText;
        }else{
            if ("details".equalsIgnoreCase(message.filtered)){
                message.messageType = MessageType.sendReq;

            } else if ("signup".equalsIgnoreCase(message.filtered)){
                message.messageType = MessageType.signUp;

            } else if ("MAKAAN_PROPERTY_BUY".equalsIgnoreCase(message.filtered)){
                message.messageType = MessageType.propertyOverview;

            } else if ("MAKAAN_LOCALITY_RESIDENTIAL_BUY".equalsIgnoreCase(message.filtered)){
                message.messageType = MessageType.localityOverview;
            }
        }

        IncomingMessageEvent event = new IncomingMessageEvent();
        event.message = message;
        eventBus.post(event);

        return result;
    }
}
