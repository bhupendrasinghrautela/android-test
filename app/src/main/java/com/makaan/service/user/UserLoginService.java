package com.makaan.service.user;

import android.text.TextUtils;

import com.makaan.constants.ApiConstants;
import com.makaan.event.user.UserLoginCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.service.MakaanService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunil on 25/01/16.
 */
public class UserLoginService implements MakaanService{

    private final static String TAG = UserLoginService.class.getSimpleName();

    public void loginWithMakaanAccount(final String uName, final String pwd) {
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(pwd)) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        String url = ApiConstants.MAKAAN_LOGIN;
        Map<String, String> pars = new HashMap<String, String>();
        pars.put("username", uName);
        pars.put("password", pwd);
        pars.put("domainId", "1");

        MakaanNetworkClient.getInstance().loginPost(url,pars,new UserLoginCallback(),TAG);
    }

    public void loginWithGoogleAccount(final String token) {
        if (TextUtils.isEmpty(token)) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        try {
            String requestUrl = buildGoogleSignInRequest(token);
            MakaanNetworkClient.getInstance().post(requestUrl,null,new UserLoginCallback(),TAG);
        } catch (IllegalArgumentException e) {
            //TODO Display an error here
        }
    }

    public void loginWithFacebookAccount(final String token) {
        if (TextUtils.isEmpty(token)) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        try {
            String requestUrl = buildFacebookSignInRequest(token);
            MakaanNetworkClient.getInstance().post(requestUrl, null, new UserLoginCallback(), TAG);
        } catch (IllegalArgumentException e) {
            //TODO Display an error here
        }
    }

    /*private static String buildMakaanSignInRequest() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(ApiConstants.BASE_URL);
        queryBuilder.append("/madrox/app/v1//login");
        return queryBuilder.toString();
    }*/


    public static String buildGoogleSignInRequest(String token) {
        if (TextUtils.isEmpty(token)) {
            throw new IllegalArgumentException("Please supply proper arguments");
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(ApiConstants.GOOGLE_LOGIN);
        queryBuilder.append(token);
        queryBuilder.append("&domainId=1&rememberme=true");

        return queryBuilder.toString();
    }


    public static String buildFacebookSignInRequest(String token) {
        if (TextUtils.isEmpty(token)) {
            throw new IllegalArgumentException("Please supply proper arguments");
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(ApiConstants.FACEBOOK_LOGIN);
        queryBuilder.append(token);
        queryBuilder.append("&domainId=1&rememberme=true");

        return queryBuilder.toString();
    }
}
