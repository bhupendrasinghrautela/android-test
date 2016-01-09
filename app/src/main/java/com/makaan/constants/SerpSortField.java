package com.makaan.constants;

import static com.makaan.constants.ResponseConstants.*;

/**
 * Created by vaibhav on 06/01/16.
 *
 * to be used on ui
 */
public enum SerpSortField {

    PRICE(1, "Price", ResponseConstants.PRICE),
    CONS_STATUS(2, "Construction Status", CONS_STATUS_ID);


    private int uid;
    private String label;
    private String sortField;

    SerpSortField(int uid, String label, String sortField) {
        this.uid = uid;
        this.label = label;
        this.sortField = sortField;
    }


    public static String getSortField(int uid) {

        for (SerpSortField serpSortField : SerpSortField.values()) {
            if (uid == serpSortField.uid) {
                return serpSortField.sortField;
            }
        }

        return null;
    }
}
