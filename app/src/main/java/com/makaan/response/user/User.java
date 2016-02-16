package com.makaan.response.user;

import java.util.ArrayList;

/**
 * Created by vaibhav on 17/01/16.
 */
public class User {
    public Long id;
    public String name;
    public String fullName;
    public String profilePictureURL;
    public ArrayList<ContactNumber> contactNumbers = new ArrayList<>();
}
