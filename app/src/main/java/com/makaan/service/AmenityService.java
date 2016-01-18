package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.amenity.AmenityCallback;
import com.makaan.response.city.City;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;



/**
 * Created by sunil on 17/01/16.
 */
public class AmenityService implements MakaanService{


    //http://marketplace-qa.makaan-ws.com/app/v1/amenity?latitude=13.03244019&longitude=77.6019516&distance=3&start=0&rows=99&sourceDomain=Makaan
    public void getAmenitiesByLocation(double lat, double lon, int distance) {

        String amenityRequestUrl = buildUrl(lat,lon,distance);

        MakaanNetworkClient.getInstance().get(amenityRequestUrl, new AmenityCallback());

    }

    private String buildUrl(double lat, double lon, int distance){
        StringBuilder urlBuilder = new StringBuilder("http://marketplace-qa.makaan-ws.com/app/v1/amenity?latitude=");
        urlBuilder.append(lat);
        urlBuilder.append("&longitude=");
        urlBuilder.append(lon);
        urlBuilder.append("&distance=");
        urlBuilder.append(distance);
        urlBuilder.append("&start=0&rows=150&sourceDomain=Makaan");
        return urlBuilder.toString();
    }
}
