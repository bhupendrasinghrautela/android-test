package com.makaan.jarvis;

/**
 * Created by sunil on 07/01/16.
 */
public class JarvisConstants {
    //http://52.76.67.48 - mp-chat
    //http://52.74.104.92 - beta-mpchat
    public static final String MESSAGE = "message";
    public static final String APPLIED_FILTER = "appliedFilter";
    public static final String FILTERED = "filtered";
    public static final String PARTICIPANT = "participant";
    public static final String TIMESTAMP = "timestamp";
    public static final String CHAT_OBJECT = "chatObj";

    //public static final String CHAT_SERVER_URL = "https://beta-mpchat.makaan-ws.com";
    public static final String CHAT_SERVER_URL = "https://chat.makaan.com";
    //public static final String CHAT_SERVER_URL = "http://10.10.2.254:8100";
    public static String DELIVERY_ID = "";
    public static final int CHAT_CARD_DEFAULT_LEFT_MARGIN=20;
    public static final int CHAT_CARD_DEFAULT_TOP_BOTTOM_MARGIN=20;

    public static int JARVIS_ACTION_DISMISS_TIMEOUT = 10000;
    public static int JARVIS_USER_ACTIVITY_TIMEOUT = 20000;
}
