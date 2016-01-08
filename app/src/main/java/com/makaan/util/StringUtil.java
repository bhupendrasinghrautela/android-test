package com.makaan.util;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aishwarya on 23/10/15.
 */
public class StringUtil {

    public static String convertStringArrayToSingleString(List<String >stringList,String delimiter){
        if(stringList == null || stringList.isEmpty() || delimiter == null){
            return null;
        }
        String prefix = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringList) {
            stringBuilder.append(prefix);
            prefix = delimiter;
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    public static String convertIntegerArrayToSingleString(List<Integer >stringList,String delimiter){
        if(stringList == null|| stringList.isEmpty() || delimiter == null){
            return null;
        }
        String prefix = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : stringList) {
            stringBuilder.append(prefix);
            prefix = delimiter;
            stringBuilder.append(integer);
        }
        return stringBuilder.toString();
    }

    public static List<Integer> getIntegerArrayFromSingleString(String string,String delimiter){
        if(string == null || delimiter == null){
            return null;
        }
        String[] stringArray = string.split(delimiter);
        List<Integer> intList = new ArrayList<Integer>(stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            String numberAsString = stringArray[i];
            intList.add(Integer.parseInt(numberAsString));
        }
        return intList;
    }

    public static List<String> getStringArrayFromSingleString(String string,String delimiter){
        if(string == null || delimiter == null){
            return null;
        }
        String[] stringArray = string.split(delimiter);
        List<String> stringList = new ArrayList<String>(stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            String stringValue = stringArray[i];
            stringList.add(stringValue);
        }
        return stringList;
    }

    public static String getDisplayPrice(double price) {
        try {
            if (price == 0) {
                return "Price on request";
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            priceStringBuilder.append("\u20B9");
            double displayPrice = (double) price / 100000.00;

            if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "Price on request";
                }
                priceStringBuilder.append(" L");
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                displayPrice = displayPrice / 100;
                try {
                    priceStringBuilder.append(String.format("%.2f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "Price on request";
                }
                priceStringBuilder.append(" Cr");
            }

            return priceStringBuilder.toString();
        } catch (Exception e) {
        } catch (Error er) {
        }
        return "Price on request";
    }

    public static boolean isValidKeyword(String inputString, String allowedRegex) {
        if (!TextUtils.isEmpty(inputString)) {
            if (inputString.matches(allowedRegex)) {
                return true;
            }
        }
        return false;
    }
}
