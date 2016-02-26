package com.makaan.util;

import com.squareup.otto.ThreadEnforcer;

/**
 * Created by vaibhav on 23/12/15.
 */
public class AppBus {

    private static final MakaanBus BUS = new MakaanBus(ThreadEnforcer.ANY);


    public static MakaanBus getInstance() {


        return BUS;
    }
}
