package com.makaan.jarvis;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.message.JoinUser;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.SocketMessage;
import com.makaan.util.AppBus;
import com.makaan.util.JsonBuilder;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisSocket {

    private boolean mTyping = false;
    private static int index = 0;
    private static Bus eventBus = AppBus.getInstance();


    public Socket mSocket; {
        try {

            mSocket = IO.socket(JarvisConstants.CHAT_SERVER_URL);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(){
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("user-acquired", onUserAcquired);
        mSocket.on("new-message-for-user", onNewMessageForUser);
        mSocket.on("agent-confirms-user", onAgentConfirmUser);
        mSocket.connect();
        joinUser();
    }

    public void close(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("user-acquired", onUserAcquired);
        mSocket.off("new-message-for-user", onNewMessageForUser);
        mSocket.off("agent-confirms-user", onAgentConfirmUser);
    }

    private void leave() {
        mSocket.disconnect();
        mSocket.connect();
    }

    private void joinUser(){
        try {
            mSocket.emit("join-user", JsonBuilder.toJson(new JoinUser()), new Ack() {
                @Override
                public void call(Object... args) {
                    Log.e("join user", "callback");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(SocketMessage message){
        message.index = index;
        message.deliveryId = JarvisConstants.DELIVERY_ID;
        index++;

        try {
            mSocket.emit("new-message-for-agent", JsonBuilder.toJson(message), new Ack() {
                @Override
                public void call(Object... args) {
                    Log.e("message to agent", "callback");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void userConfirmsAgent(Object data){

        try {
            mSocket.emit("user-confirms-agent", JsonBuilder.toJson(data), new Ack() {
                @Override
                public void call(Object... args) {
                    Log.e("message to agent", "callback");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handleMessage(args);
        }
    };


    private Emitter.Listener onUserAcquired = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handleMessage(args);
        }
    };

    private Emitter.Listener onNewMessageForUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(null!=args || args.length>0) {
                IncomingMessageEvent event = new IncomingMessageEvent();
                event.message = parseMessage((JSONObject) args[0]);
                eventBus.post(event);
                userConfirmsAgent(args[0]);
            }
        }
    };

    private Emitter.Listener onAgentConfirmUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handleMessage(args);
        }
    };


    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    private void handleMessage(Object... args)  {
        Log.e("Message", "dummy");
    }

    private SocketMessage parseMessage(JSONObject object){
        Message message = new Message();
        message.message = object.optString("message");
        message.appliedFilter = object.optBoolean("appliedFilter");
        message.filtered = object.optString("filtered");
        return message;
    }


}