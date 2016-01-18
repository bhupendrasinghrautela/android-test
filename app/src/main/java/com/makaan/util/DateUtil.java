package com.makaan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aishwarya on 06/01/16.
 */
public class DateUtil {

    private static final String MONTH_YEAR_DATE_FORMAT = "MMMM yyyy";

    public static String getMonthYearDisplayDate(long timestamp) {
        if(timestamp == 0) {
            return "";
        }

        final SimpleDateFormat format = new SimpleDateFormat(MONTH_YEAR_DATE_FORMAT, Locale.ENGLISH);
        return format.format(new Date(timestamp));
    }
}
