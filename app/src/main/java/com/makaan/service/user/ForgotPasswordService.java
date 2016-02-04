package com.makaan.service.user;

import com.makaan.constants.ApiConstants;
import com.makaan.event.user.ForgotPasswordCallBack;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.service.MakaanService;

/**
 * Created by makaanuser on 14/1/16.
 */
public class ForgotPasswordService implements MakaanService{

    public static String TAG=ForgotPasswordService.class.getSimpleName();

    public ForgotPasswordService(){

    }

    public void forgotPasswordRequest(String email) {

        try {
            MakaanNetworkClient.getInstance().socialLoginPost(buildForgotPasswordRequest() + email
                    + "&sourceDomain=Makaan", new ForgotPasswordCallBack(), TAG);
        } catch (Exception e) {
            //TODO Display an error here
        }

    }

    public static String buildForgotPasswordRequest() {
        return ApiConstants.BASE_URL + ApiConstants.FORGOT_PASSWORD;
    }
}
