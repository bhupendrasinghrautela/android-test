package com.makaan.jarvis;

import android.text.TextUtils;

import com.makaan.MakaanBuyerApplication;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.event.ChatHistoryEvent;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.jarvis.event.OutgoingMessageEvent;
import com.makaan.jarvis.message.ChatHistoryData;
import com.makaan.jarvis.message.ChatMessages;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
import com.makaan.jarvis.message.MessageUtil;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.JsonParser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisClient {

    private static JarvisSocket jarvisSocket;
    private static Bus eventBus;
    private static JarvisClient mInstance;
    private static ChatMessages mChatMessages;
    private boolean isFetching = false;
    private static Map<String, Integer> jarvisMessageTypeMap =
            MasterDataCache.getInstance().getJarvisMessageTypeMap();

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
        fetchChatHistory();
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

    public boolean getFetchingStatus(){
        return isFetching;
    }

    public void fetchChatHistory() {
        isFetching = true;
        ChatHistoryService chatHistoryService =
                (ChatHistoryService) MakaanServiceFactory.getInstance().getService(ChatHistoryService.class);

        chatHistoryService.fetchChatHistory(JarvisConstants.DELIVERY_ID, new StringRequestCallback() {
            @Override
            public void onSuccess(String response) {
                if(TextUtils.isEmpty(response)){
                    //jarvisSocket.checkAvailable();
                    isFetching = false;
                    return;
                }

                try {
                    JSONObject topJSOnObject = new JSONObject(response);
                    JSONArray data = topJSOnObject.optJSONArray("data");


                    if(data!=null && data.length()>0) {

                        List<Message> messages = new ArrayList<Message>();
                        for (int i=0; i<data.length();i++) {
                            Message message = (Message) MessageUtil.parseMessage(data.getJSONObject(i));
                            if (message.participant == 0) {
                                message.messageType = MessageType.outText;
                            }else if (TextUtils.isEmpty(message.filtered) || message.isAgentAvailableMessage) {
                                message.messageType = MessageType.inText;

                            } else {
                                Integer type = jarvisMessageTypeMap.get(message.filtered);
                                if (null == type) {
                                    continue;
                                }
                                message.messageType = MessageType.fromInt(type);

                            }
                            messages.add(message);
                        }

                        if(!messages.isEmpty()) {
                            getChatMessages().clear();
                            getChatMessages().addAll(messages);

                            ChatHistoryEvent chatHistoryEvent = new ChatHistoryEvent();
                            chatHistoryEvent.isError = false;
                            AppBus.getInstance().post(chatHistoryEvent);
                        }
                    }

                }catch(Exception e){}

                finally {
                    jarvisSocket.checkAvailable();
                    isFetching = false;
                }
            }

            @Override
            public void onError(ResponseError error) {
                isFetching = false;
                jarvisSocket.checkAvailable();
            }
        });
    }

}
