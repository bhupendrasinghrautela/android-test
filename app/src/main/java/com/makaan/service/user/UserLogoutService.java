package com.makaan.service.user;

import com.makaan.constants.ApiConstants;
import com.makaan.event.user.UserLogoutCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.service.MakaanService;

/**
 * Created by Sandeep on 19/2/16.
 */
public class UserLogoutService implements MakaanService {

    private final String TAG = UserLogoutService.class.getSimpleName();


    public void makeLogoutRequest() {
        try{
            MakaanNetworkClient.getInstance().post(buildLogoutUrl(), null, new UserLogoutCallback(), TAG);
        }catch (Exception e){
            //TODO Display Error
        }
    }

    private static String buildLogoutUrl() {
        return ApiConstants.BASE_URL+"/app/v1/logout";
    }
}
