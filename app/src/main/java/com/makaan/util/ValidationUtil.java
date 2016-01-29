package com.makaan.util;

import android.text.TextUtils;

/**
 * Created by makaanuser on 25/1/16.
 */
public class ValidationUtil {

    public static boolean isValidEmail(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPhoneNumber(CharSequence target , String countryName) {

        if(countryName.equalsIgnoreCase("India")){
            if(target.length()==10 && Integer.parseInt(String.valueOf(String.valueOf(target).charAt(0)))>=7 ){
                return true;
            }
            else {
                return false;
            }
        }
        else if(!countryName.equalsIgnoreCase("India") && target.length()>=6 && target.length()<=12){
            return true;
        }
        else {
            return false;
        }

    }
}
