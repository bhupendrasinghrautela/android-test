package com.makaan.jarvis.message;

/**
 * Created by sunil on 27/01/16.
 */
public enum CtaType {

    serpScroll(0), enquiryDropped(1), contentPyr(2), childSerp(3);

    public int value;

    CtaType(int value){
        this.value = value;
    }

    public static CtaType fromInt(int i) {
        for (CtaType type : CtaType.values()) {
            if(i==type.value){
                return type;
            }
        }

        return null;
    }
}
