package com.makaan.service;

import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.amenity.AmenityCallback;


/**
 * Created by sunil on 17/01/16.
 */
public class AmenityService implements MakaanService{

    public enum EntityType{
        PROJECT,LOCALITY
    }


    //http://marketplace-qa.makaan-ws.com/app/v1/amenity?latitude=13.03244019&longitude=77.6019516&distance=3&start=0&rows=99&sourceDomain=Makaan
    public void getAmenitiesByLocation(double lat, double lon, int distance, EntityType entityType) {


        String amenityRequestUrl = buildUrl(lat,lon,distance);

        MakaanNetworkClient.getInstance().get(amenityRequestUrl, new AmenityCallback(entityType));

    }

    private String buildUrl(double lat, double lon, int distance){
        StringBuilder urlBuilder = new StringBuilder(ApiConstants.AMENITIES);
        urlBuilder.append(lat);
        urlBuilder.append("&longitude=");
        urlBuilder.append(lon);
        urlBuilder.append("&distance=");
        urlBuilder.append(distance);
        urlBuilder.append("&start=0&rows=150&sourceDomain=Makaan");
        return urlBuilder.toString();
    }
}
