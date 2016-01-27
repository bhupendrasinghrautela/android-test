package com.makaan.jarvis.message;

/**
 * Created by sunil on 12/01/16.
 */
public enum MessageType {

    //TODO WIP , to be defined in config

    outText(0), inText(1), projectOverview(2), sellerOverView(3), sendReq(4),
    signUp(5), propertyOverview(6), localityOverview(7), plainLink(8);

    public int value;

    MessageType(int value){
        this.value = value;
    }

    public static MessageType fromInt(int i) {
        for (MessageType type : MessageType.values()) {
            if(i==type.value){
                return type;
            }
        }

        return null;
    }
}
