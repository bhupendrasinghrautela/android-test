package com.makaan.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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


    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);
        return cal;
    }
}
