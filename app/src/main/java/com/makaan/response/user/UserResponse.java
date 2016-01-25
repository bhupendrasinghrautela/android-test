package com.makaan.response.user;

import com.makaan.response.BaseResponse;

/**
 * Created by sunil on 25/01/16.
 */
public class UserResponse extends BaseResponse {

    private UserData data;

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public static class UserData{
        public int id;
        public String email;
        public String firstName;
        public Integer countryId;
        public String lastName;
        public String contactNumber;
        public String profileImageUrl;
        public int favCount;
        public String createdAt;
        public String secondaryEmail;
        public Boolean notificationOn;


    }
}