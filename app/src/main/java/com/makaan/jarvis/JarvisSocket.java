package com.makaan.jarvis;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.makaan.jarvis.message.JoinUser;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;

import org.json.JSONException;

import java.net.URISyntaxException;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisSocket {

    private boolean mTyping = false;


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
        mSocket.connect();

        try {
            mSocket.emit("join-user", JsonBuilder.toJson(new JoinUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void close(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("user-acquired", onUserAcquired);
        mSocket.off("new-message-for-user", onNewMessageForUser);
    }

    private void leave() {
        mSocket.disconnect();
        mSocket.connect();
    }


    public void sendMessage(String message){

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

        try {
            Log.e("Message", "dummy");
            //JSONObject object = new JSONObject();
            //object.put("id",1000);
            //mSocket.emit("hi", object);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
