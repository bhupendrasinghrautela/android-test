package com.makaan.util;

import com.squareup.otto.Bus;

/**
 * Created by vaibhav on 23/12/15.
 */
public class AppBus {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}
