package com.makaan.jarvis;

import android.os.Handler;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.JoinUser;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.SocketMessage;
import com.makaan.util.AppBus;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisSocket {

    private boolean mTyping = false;
    private static int index = 0;
    private boolean isConnectionError = false;
    private Object agentData;
    private boolean agentLost = false;
    private boolean isAcquired = false;

    private Runnable mTimeoutRunnable;
    private Handler mTimeoutHandler =new Handler();

    public Socket mSocket; {
        try {

            mSocket = IO.socket(JarvisConstants.CHAT_SERVER_URL);
            AppBus.getInstance().register(this);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mTimeoutRunnable=new Runnable() {

            @Override
            public void run() {
                mSocket.emit("automatic-transfer",  new Ack() {
                    @Override
                    public void call(Object... args) {

                    }
                });
            }
        };
    }

    public void open(){
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("user-acquired", onUserAcquired);
        mSocket.on("new-message-for-user", onNewMessageForUser);
        mSocket.on("expose-session", onExposeSession);
        mSocket.on("agent-confirms-user", onAgentConfirmUser);
        //mSocket.on("agent-left", onAgentLeft);
        //mSocket.on("acquire-again", onAgentAcquireAgain);
        mSocket.connect();
        joinUser();
    }

    public void close(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("user-acquired", onUserAcquired);
        mSocket.off("new-message-for-user", onNewMessageForUser);
        mSocket.off("expose-session", onExposeSession);
        mSocket.off("agent-confirms-user", onAgentConfirmUser);
        //mSocket.off("agent-left", onAgentLeft);
        //mSocket.off("acquire-again", onAgentAcquireAgain);
    }

    public void refresh() {
        if(isConnectionError){
            isConnectionError=false;
            if(null==mSocket || !mSocket.connected()){
                try {
                    mSocket = IO.socket(JarvisConstants.CHAT_SERVER_URL);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            close();
            open();
        }
    }

    private void joinUser(){
        try {
            mSocket.emit("join-user", JsonBuilder.toJson(new JoinUser(agentData, isAcquired)), new Ack() {
                @Override
                public void call(Object... args) {
                    Log.e("join user", "user");
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
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void userConfirmsAgent(Object data){

        try {
            mSocket.emit("user-confirms-agent", data, new Ack() {
                @Override
                public void call(Object... args) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rateAgent(float rating){

        try {
            JSONObject data = new JSONObject();
            data.put("deliveryId", JarvisConstants.DELIVERY_ID);
            data.put("rating", rating);
            data.put("agent","acquired");

            mSocket.emit("done-rating", data, new Ack() {
                @Override
                public void call(Object... args) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnectionError = true;
            handleMessage(args);
        }
    };


    private Emitter.Listener onUserAcquired = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                isAcquired = true;
                JSONObject jsonObject = (JSONObject) args[0];
                agentData = jsonObject.get("userDetails");
            } catch (Exception e) {
                e.printStackTrace();
            }
            handleMessage(args);
        }
    };

    private Emitter.Listener onNewMessageForUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(null!=args || args.length>0) {
                userConfirmsAgent(args[0]);
                SocketMessage message = parseMessage((JSONObject) args[0]);
                JarvisClient.getInstance().getChatMessages().add((Message) message);
            }
        }
    };

    private Emitter.Listener onExposeSession = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(null!=args || args.length>0) {
                Map<String, Integer> jarvisCtaMessageTypeMap =
                        MasterDataCache.getInstance().getJarvisCtaMessageTypeMap();
                ExposeMessage message = parseExposeMessage((JSONObject) args[0]);

                if(null==message){
                    return;
                }
                Integer type = jarvisCtaMessageTypeMap.get(message.properties.type);

                if(null!=type) {
                    message.properties.ctaType = CtaType.fromInt(type);
                    if(null!=message.properties.ctaType) {
                        OnExposeEvent event = new OnExposeEvent();
                        event.message = message;
                        AppBus.getInstance().post(event);
                    }
                }
            }
        }
    };

    private Emitter.Listener onAgentLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                if(null!=agentData) {
                    JSONObject jsonObject = (JSONObject) args[0];
                    JSONObject serverAgent = (JSONObject) jsonObject.get("userDetails");
                    JSONObject currentAgent = (JSONObject) agentData;

                    if(null==serverAgent){
                        return;
                    }

                    if(currentAgent.optInt("id") == serverAgent.optInt("id")) {
                        agentLost = true;
                        mTimeoutHandler.postDelayed(mTimeoutRunnable, 10000);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onAgentConfirmUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            handleMessage(args);
        }
    };

    private Emitter.Listener onAgentAcquireAgain = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(agentLost) {
                agentLost = false;
                mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
            }
        }
    };


    private void handleMessage(Object... args)  {
        Log.e("Handle message : " , args.toString());
    }

    private SocketMessage parseMessage(JSONObject object){
        Message message = new Message();
        message.message = object.optString(JarvisConstants.MESSAGE);
        message.appliedFilter = object.optBoolean(JarvisConstants.APPLIED_FILTER);
        message.filtered = object.optString(JarvisConstants.FILTERED);

        String chatObjString = object.optString(JarvisConstants.CHAT_OBJECT);
        ChatObject chatObject = (ChatObject) JsonParser.parseJson(chatObjString.toString(), ChatObject.class);
        message.chatObj = chatObject;

        return message;
    }

    private ExposeMessage parseExposeMessage(JSONObject object){
        ExposeMessage message = (ExposeMessage) JsonParser.parseJson(object.toString(), ExposeMessage.class);
        return message;
    }


}
