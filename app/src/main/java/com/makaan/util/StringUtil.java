package com.makaan.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            if(price == Double.NaN) {
                return String.valueOf(price);
            }
            if (price == 0) {
                return "Price on request";
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            priceStringBuilder.append("\u20B9");
            double displayPrice = (double) price / 100000.00;

            if(displayPrice < 1) {
                displayPrice = (double) price / 1000.00;
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "Price on request";
                }
                priceStringBuilder.append(" K");
            } else if (displayPrice < 100.0) {
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
            //TODO: log atleast
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

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    public static String getTwoDecimalPlaces(Double num){
        return  new DecimalFormat("#.##").format(num);
    }
}
