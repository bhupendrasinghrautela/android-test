package com.makaan.jarvis.message;

import com.makaan.jarvis.JarvisConstants;
import com.makaan.util.JsonParser;

import org.json.JSONObject;

/**
 * Created by sunil on 10/03/16.
 */
public class MessageUtil {

    public static SocketMessage parseMessage(JSONObject object){
        Message message = new Message();
        message.message = object.optString(JarvisConstants.MESSAGE);
        message.appliedFilter = object.optBoolean(JarvisConstants.APPLIED_FILTER);
        message.filtered = object.optString(JarvisConstants.FILTERED);
        message.participant = object.optInt(JarvisConstants.PARTICIPANT);
        message.timestamp = object.optLong(JarvisConstants.TIMESTAMP);

        String chatObjString = object.optString(JarvisConstants.CHAT_OBJECT);
        ChatObject chatObject = (ChatObject) JsonParser.parseJson(chatObjString.toString(), ChatObject.class);
        message.chatObj = chatObject;

        return message;
    }
}
