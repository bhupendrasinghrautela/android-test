package com.makaan.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by sunil on 26/02/16.
 */
public class MakaanBus extends Bus {

    public MakaanBus() {
    }

    public MakaanBus(String identifier) {
        super(identifier);
    }

    public MakaanBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }

    public MakaanBus(ThreadEnforcer enforcer, String identifier) {
        super(enforcer, identifier);
    }

    @Override
    public void register(Object object) {
        try {
            super.register(object);
        } catch (Throwable t) {

        }
    }

    @Override
    public void unregister(Object object) {
        try {
            super.unregister(object);
        } catch (Throwable t) {

        }
    }
}
