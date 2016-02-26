package com.makaan.jarvis.message;

/**
 * Created by sunil on 25/02/16.
 */
public enum PageVisitType {

    siteVisit(0), book(1), register(2), posses(3);

    public int value;

    PageVisitType(int value){
        this.value = value;
    }

    public static PageVisitType fromInt(int i) {
        for (PageVisitType type : PageVisitType.values()) {
            if(i==type.value){
                return type;
            }
        }

        return null;
    }
}
