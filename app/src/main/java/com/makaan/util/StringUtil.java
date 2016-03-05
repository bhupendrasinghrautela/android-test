package com.makaan.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by aishwarya on 23/10/15.
 */
public class StringUtil {

    private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("en", "IN"));

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
            if(Double.isNaN(price)) {
                // TODO check for fail safe value
                return String.valueOf(price);
            }
            if (price == 0) {
//                return "Price on request";
                return "0";
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            priceStringBuilder.append("\u20B9");
            double displayPrice = (double) price / 100000.00;

            if(displayPrice < 1) {
                displayPrice = (double) price / 1000.00;
                DecimalFormat df = new DecimalFormat("#.##");
                try {
                    priceStringBuilder.append(String.format("%.2f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "Price on request";
                }
                priceStringBuilder.append(" K");
            } else if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.##");
                try {
                    priceStringBuilder.append(String.format("%.2f",
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

    public static String getDisplayPriceSingle(double price) {
        try {
            if(Double.isNaN(price)) {
                // TODO check for fail safe value
                return String.valueOf(price);
            }
            if (price == 0) {
//                return "Price on request";
                return "0";
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            priceStringBuilder.append("\u20B9");
            double displayPrice = (double) price / 100000.00;

            if(displayPrice < 1) {
                displayPrice = (double) price / 1000.00;
                DecimalFormat df = new DecimalFormat("#.##");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "Price on request";
                }
                priceStringBuilder.append(" K");
            } else if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.##");
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
                    priceStringBuilder.append(String.format("%.1f",
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

    public static String getDisplayPriceForChart(double price) {
        try {
            if(Double.isNaN(price)) {
                // TODO check for fail safe value
                return String.valueOf(price);
            }
            if (price == 0) {
                return "0";
            }
            StringBuilder priceStringBuilder = new StringBuilder();
            if(price<1000){
                return String.valueOf(String.format("%.0f",price));
            }
            double displayPrice = (double) price / 100000.00;

            if(displayPrice < 1) {
                displayPrice = (double) price / 1000.00;
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "0";
                }
                priceStringBuilder.append("K");
            } else if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "0";
                }
                priceStringBuilder.append("L");
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                displayPrice = displayPrice / 100;
                try {
                    priceStringBuilder.append(String.format("%.2f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return "0";
                }
                priceStringBuilder.append("Cr");
            }

            return priceStringBuilder.toString();
        } catch (Exception e) {
            //TODO: log atleast
        }
        return "0";
    }

    public static float getPriceForChart(double price) {
        try {
            StringBuilder priceStringBuilder = new StringBuilder();
            double displayPrice = (double) price / 100000.00;

            if(displayPrice < 1) {
                displayPrice = (double) price / 1000.00;
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    return Float.parseFloat(df.format(displayPrice));
                } catch (NumberFormatException nfe) {
                    return 0;
                }
            } else if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    return Float.parseFloat(df.format(displayPrice));
                } catch (NumberFormatException nfe) {
                    return 0;
                }
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                displayPrice = displayPrice / 100;
                try {
                    return Float.parseFloat(df.format(displayPrice));
                } catch (NumberFormatException nfe) {
                    return 0;
                }
            }
        } catch (Exception e) {
            //TODO: log atleast
        }
        return 0;
    }

    public static SpannableString getSpannedPrice(double price) {
        try {
            if(Double.isNaN(price)) {
                // TODO check for fail safe value
                return new SpannableString(String.valueOf(price));
            }
            if (price == 0) {
                return new SpannableString("Price on request");
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
                    return new SpannableString("Price on request");
                }
                priceStringBuilder.append(" K");
            } else if (displayPrice < 100.0) {
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    priceStringBuilder.append(String.format("%.1f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return new SpannableString("Price on request");
                }
                priceStringBuilder.append(" L");
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                displayPrice = displayPrice / 100;
                try {
                    priceStringBuilder.append(String.format("%.2f",
                            Double.parseDouble(df.format(displayPrice))));
                } catch (NumberFormatException nfe) {
                    return new SpannableString("Price on request");
                }
                priceStringBuilder.append(" Cr");
            }
            SpannableString spannableString=  new SpannableString(priceStringBuilder);
            spannableString.setSpan(new RelativeSizeSpan(0.8f), 0, 1, 0); // set size
            spannableString.setSpan(new RelativeSizeSpan(0.8f),
                    priceStringBuilder.length()-2,priceStringBuilder.length() , 0);
            return spannableString;
        } catch (Exception e) {
            //TODO: log atleast
        }
        return new SpannableString("Price on request");
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

    /**
     * Get age string from its timestamp, string will be returned based on the type parameter
     * @param time timestamp in milliseconds
     * @param type Calendar.MONTH, Calendar.YEAR, Calendar.DAY_OF_MONTH
     * @return
     */
    public static String getAgeFromTimeStamp(Long time, int type) {
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        long age = currentTime - time;

        cal.setTimeInMillis(age);

        StringBuilder builder = new StringBuilder();
        if((cal.get(Calendar.YEAR) - 1970) <= 1) {
            builder.append(String.format("%d yr", cal.get(Calendar.YEAR) - 1970));
        } else {
            builder.append(String.format("%d yrs", cal.get(Calendar.YEAR) - 1970));
        }

        if(type == Calendar.MONTH || type == Calendar.DAY_OF_MONTH) {
            if(cal.get(Calendar.YEAR) <= 1) {
                builder.append(String.format(", %d month", cal.get(Calendar.MONTH)));
            } else {
                builder.append(String.format(", %d months", cal.get(Calendar.MONTH)));
            }
            if(type == Calendar.DAY_OF_MONTH) {
                if(cal.get(Calendar.YEAR) <= 1) {
                    builder.append(String.format(", %d day", cal.get(Calendar.DAY_OF_MONTH)));
                } else {
                    builder.append(String.format(", %d days", cal.get(Calendar.DAY_OF_MONTH)));
                }
            }
        }
        return builder.toString();
    }

    public static String getFormattedNumber(double in) {
        return numberFormatter.format(in);
    }
}
