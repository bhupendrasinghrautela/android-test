package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.user.CompanyUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.makaan.constants.RequestConstants.FILTERS;
import static com.makaan.constants.RequestConstants.USER_ID;

/**
 * Created by vaibhav on 17/01/16.
 */
public class UserService implements  MakaanService{


    public void getCompanyUsers(List<Long> userIds) {
        StringBuilder url = new StringBuilder(ApiConstants.COMPANY_USERS).append("?").append(FILTERS).append("=");
        int size = userIds.size();
        for (int i = 0; i < size; i++) {
            Long userId = userIds.get(i);
            if (i != size - 1) {
                url.append(USER_ID).append("==").append(userId).append(",");
            } else {
                url.append(USER_ID).append("==").append(userId);
            }
        }

        Type companyUserListType = new TypeToken<ArrayList<CompanyUser>>() {
        }.getType();
        MakaanNetworkClient.getInstance().get(url.toString(), companyUserListType, new ObjectGetCallback() {
            @Override
            public void onError(ResponseError error) {
                //TODO Handle error
            }

            @Override
            public void onSuccess(Object responseObject) {
                ArrayList<CompanyUser> companyUsers = (ArrayList<CompanyUser>) responseObject;
                //TODO: raise event

            }
        }, true);

        //JsonObjectRequest request = new JsonObjectRequest(url.toString(), null, future, future);
        /*RequestFuture<JSONObject> future = RequestFuture.newFuture();
        try {
            JSONObject response = future.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }*/
    }
}
