package com.makaan.jarvis.message;

/**
 * Created by sunil on 12/01/16.
 */
public enum MessageType {

    //TODO WIP , to be defined in config

    outText(0), inText(1), projectOverview(2), sellerOverView(3), askReq(4), sendReq(5),
    signUp(6), propertyOverview(7), localityOverview(8), localityBuy(9), localityRent(10),
    plainLink(11), sellerSerp(12), sellerSerpMap(13), agentRating(14), suburbResidentialBuy(15),
    suburbRent(16), suburbBuy(17), cityRent(18), cityBuy(19);

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
