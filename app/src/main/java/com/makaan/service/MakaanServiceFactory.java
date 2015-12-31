package com.makaan.service;

import java.util.HashMap;

/**
 * Created by vaibhav on 31/12/15.
 */
public class MakaanServiceFactory {

    private static MakaanServiceFactory makaanServiceFactory = new MakaanServiceFactory();

    private MakaanServiceFactory() {

    }

    public static MakaanServiceFactory getInstance() {
        return makaanServiceFactory;
    }

    public HashMap<Class, MakaanService> serviceHashMap = new HashMap<Class, MakaanService>();

    public MakaanService getService(Class classString) {
        return serviceHashMap.get(classString);
    }


    public MakaanServiceFactory registerService(Class classString, MakaanService makaanService) {
        serviceHashMap.put(classString, makaanService);
        return this;
    }
}
