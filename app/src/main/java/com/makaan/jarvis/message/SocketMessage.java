package com.makaan.jarvis.message;

/**
 * Created by sunil on 14/01/16.
 */
public class SocketMessage {

    public String message;
    public String deliveryId;
    public int index;
    public boolean appliedFilter;
    public String filtered;
    public ChatObject chatObj;

    public int participant;
    public long timestamp;

    public boolean isAgentAvailableMessage;
}
