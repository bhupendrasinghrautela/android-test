package com.makaan.adapter;

/**
 * Created by sunil on 22/09/15.
 */
public enum  RecycleViewMode {
    DATA(1), FOOTER_LOADING(2), FOOTER_ERROR(3), DATA_TYPE_LISTING(4), DATA_TYPE_CLUSTER(5), DATA_TYPE_BUILDER(6), DATA_TYPE_SELLER(7),
    DATA_TYPE_COUNT(8),DATA_TYPE_NO_LISTING(9);

    int value;

    public int getValue() {
        return value;
    }

    RecycleViewMode(int type)
    {
        value = type;

    }
}
