package com.makaan.jarvis;

import android.os.Handler;

import com.crashlytics.android.Crashlytics;

import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.event.OnExposeEvent;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.JoinUser;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.SocketMessage;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisSocket {

    private boolean mTyping = false;
    private static int index = 0;
    private boolean isRefreshRequired = false;
    private Object agentData;
    private boolean agentLost = false;
    private boolean isAcquired = false;
    private boolean isAvailabilityChecked = false;

    private Runnable mTimeoutRunnable;
    private Handler mTimeoutHandler =new Handler();

    private Runnable mUserInactiveTimeoutRunnable;
    private Handler mUserInactiveTimeoutHandler =new Handler();

    private static final long AUTOMATIC_AGENT_TRANSFER_TIMEOUT = 10000;
    private static final long USER_INACTIVE_TIMEOUT = 180000;

    public Socket mSocket; {
        try {

            mSocket = IO.socket(JarvisConstants.CHAT_SERVER_URL);

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

        mUserInactiveTimeoutRunnable=new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("deliveryId",JarvisConstants.DELIVERY_ID);
                    mSocket.emit("user-inactive", jsonObject);
                    isRefreshRequired = true;
                    mUserInactiveTimeoutHandler.removeCallbacks(mUserInactiveTimeoutRunnable);

                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    CommonUtil.TLog("exception", e);
                }

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
        mSocket.on("agent-left", onAgentLeft);
        mSocket.on("acquire-again", onAgentAcquireAgain);
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
        mSocket.off("agent-left", onAgentLeft);
        mSocket.off("acquire-again", onAgentAcquireAgain);
    }

    public void refresh() {
        if(isRefreshRequired){
            isRefreshRequired =false;
            if(null==mSocket || !mSocket.connected()){
                try {
                    mSocket = IO.socket(JarvisConstants.CHAT_SERVER_URL);
                } catch (URISyntaxException e) {
                    Crashlytics.logException(e);
                    CommonUtil.TLog("exception", e);
                }
            }
            close();
            open();
        }
    }

    public void checkAvailable(){
            try {
                if(isAvailabilityChecked){
                    return;
                }

                isAvailabilityChecked = true;
                mSocket.emit("check-available",JarvisConstants.DELIVERY_ID, new Ack() {
                    @Override
                    public void call(Object... args) {
                        JSONObject jsonObject = (JSONObject) args[0];
                        String reason = jsonObject.optString("reason");
                        Message message = new Message();
                        message.message = reason;
                        message.isAgentAvailableMessage = true;
                        message.timestamp = System.currentTimeMillis();
                        JarvisClient.getInstance().getChatMessages().add(message);

                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                CommonUtil.TLog("exception", e);
            }
    }

    private void joinUser(){
        try {
            mSocket.emit("join-user", JsonBuilder.toJson(new JoinUser(agentData, isAcquired)), new Ack() {
                @Override
                public void call(Object... args) {
                    CommonUtil.TLog("join user", "user");
                    if(null!=mUserInactiveTimeoutHandler) {
                        mUserInactiveTimeoutHandler.removeCallbacks(mUserInactiveTimeoutRunnable);
                        mUserInactiveTimeoutHandler.postDelayed(mUserInactiveTimeoutRunnable, USER_INACTIVE_TIMEOUT);
                    }
                }
            });
        } catch (JSONException e) {
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
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
                    if(null!=mUserInactiveTimeoutHandler){
                        mUserInactiveTimeoutHandler.removeCallbacks(mUserInactiveTimeoutRunnable);
                        mUserInactiveTimeoutHandler.postDelayed(mUserInactiveTimeoutRunnable, USER_INACTIVE_TIMEOUT);

                    }
                }
            });
        } catch (JSONException e) {
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
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
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
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
                    //TODO might require to refresh the socket
                    //isRefreshRequired = true;
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            CommonUtil.TLog("exception", e);
        }
    }


    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isRefreshRequired = true;
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
                Crashlytics.logException(e);
                CommonUtil.TLog("exception", e);
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
                message.timestamp = System.currentTimeMillis();
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
                        mTimeoutHandler.postDelayed(mTimeoutRunnable, AUTOMATIC_AGENT_TRANSFER_TIMEOUT);
                    }
                }
            } catch (ClassCastException e) {
                if(args.length > 0 && args[0] != null) {
                    Crashlytics.log(args[0].toString());
                }
                Crashlytics.logException(e);
                CommonUtil.TLog("exception", e);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                CommonUtil.TLog("exception", e);
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
                if(null!=mTimeoutHandler && null!=mTimeoutRunnable) {
                    mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
                }
            }
        }
    };


    private void handleMessage(Object... args)  {
        CommonUtil.TLog("Handle message : " , args.toString());
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
