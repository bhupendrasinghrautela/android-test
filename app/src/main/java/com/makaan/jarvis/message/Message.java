package com.makaan.jarvis.message;

import com.makaan.jarvis.message.ChatObject;

/**
 * Created by sunil on 12/01/16.
 */
public class Message extends BaseMessage{
    public String message;
    public String deliveryId;
    public int index;
    public boolean appliedFilter;
    public ChatObject chatObject;
}
