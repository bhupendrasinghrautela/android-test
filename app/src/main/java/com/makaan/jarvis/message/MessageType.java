package com.makaan.jarvis.message;

/**
 * Created by sunil on 12/01/16.
 */
public enum MessageType {

    //TODO WIP , to be defined in config

    outText(0), inText(1), projectOverview(2), sellerOverView(3), serpFilter(4),
    signUp(5);

    public int value;

    MessageType(int value){
        this.value = value;
    }
}
