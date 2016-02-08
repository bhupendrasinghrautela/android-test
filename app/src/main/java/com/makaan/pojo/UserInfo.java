package com.makaan.pojo;


import java.util.List;

/**
 * A model for user info data
 */

public class UserInfo extends BaseJsonModel {

    private UserData data;

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public static class UserData {
        private int id;
        private String email;
        private String firstName;
        private Integer countryId;
        private String lastName;
        private String contactNumber;
        private String profileImageUrl;
        private List<String> roles;
        private int favCount;
        private String createdAt;
        private String secondaryEmail;
        private Boolean notificationOn;

        public Integer getCountryId() {
            return countryId;
        }

        public void setCountryId(Integer countryId) {
            this.countryId = countryId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastLoginAt() {
            return lastLoginAt;
        }

        public void setLastLoginAt(String lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
        }

        private String lastLoginAt;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public int getFavCount() {
            return favCount;
        }

        public void setFavCount(int favCount) {
            this.favCount = favCount;
        }

        public boolean isNotificationOn() {
            return notificationOn;
        }

        public void setNotificationOn(boolean notification) {
            this.notificationOn = notification;
        }

        public String getSecondaryEmail() {
            return secondaryEmail;
        }

        public void setSecondaryEmail(String secondaryEmail) {
            this.secondaryEmail = secondaryEmail;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}