package com.makaan.jarvis;

import android.text.TextUtils;

import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.StringRequestCallback;
import com.makaan.service.MakaanService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunil on 09/03/16.
 */
public class ChatHistoryService implements MakaanService {

    private final static String TAG = ChatHistoryService.class.getSimpleName();

    public void fetchChatHistory(String deliveryId, StringRequestCallback stringRequestCallback) {
        if (TextUtils.isEmpty(deliveryId)) {
            return;
        }

        String url = getChatHistoryUrl();
        Map<String, String> pars = new HashMap<String, String>();
        pars.put("sessionId", deliveryId);

        MakaanNetworkClient.getInstance().postFormWithParams(url,pars,stringRequestCallback,TAG);
    }

    private String getChatHistoryUrl(){
        StringBuilder builder = new StringBuilder();
        builder.append(JarvisConstants.CHAT_SERVER_URL);
        builder.append("/api/chat");
        return builder.toString();
    }
}
