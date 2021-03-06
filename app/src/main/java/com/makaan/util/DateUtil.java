package com.makaan.util;

import android.content.Context;

import com.makaan.R;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aishwarya on 06/01/16.
 */
public class DateUtil {

    private static final String MONTH_YEAR_DATE_FORMAT = "MMMM yyyy";
    private static final String MONTH_YEAR_DATE_CHART_FORMAT = "MMMyy";
    private static final String DATE_TIME = "MMM d h:mm a";

    public static String getMonthYearDisplayDate(long timestamp) {
        if(timestamp == 0) {
            return "";
        }

        final SimpleDateFormat format = new SimpleDateFormat(MONTH_YEAR_DATE_CHART_FORMAT, Locale.ENGLISH);
        return format.format(new Date(timestamp)).toLowerCase();
    }

    public static String getDateTime(long timestamp) {
        if(timestamp == 0) {
            return "";
        }

        final SimpleDateFormat format = new SimpleDateFormat(DATE_TIME, Locale.ENGLISH);
        return format.format(new Date(timestamp)).toLowerCase();
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

    public static String getDiffUsingTimeStamp(Context context,Long time){
        LocalDate original = LocalDate.fromDateFields(new Date(time));
        LocalDate now = LocalDate.now();
        Years years = Years.yearsBetween(original, now);
        if(years.getYears()>1){
            return years.getYears() +" "+ context.getString(R.string.years);
        }
        else{
            Months months = Months.monthsBetween(original,now);
            return months.getMonths()+" "+ context.getString(R.string.months);
        }
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);
        return cal;
    }

    public static Date getDateMonthsBack(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);
        cal.add(Calendar.DAY_OF_MONTH, - cal.get(Calendar.DAY_OF_MONTH) + 1);
        Date result = cal.getTime();
        return result;
    }

    public static String getTime(long timestamp) {

        long millis = System.currentTimeMillis();
        long diff = Math.abs(millis - timestamp);
        if (diff > 0) {

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if (diffDays > 1) {
                return (int) diffDays + " days ago";
            } else if (diffDays == 1) {
                return "1 day ago";
            } else if (diffHours > 1) {
                return (int) diffHours + " hrs ago";
            } else if (diffHours == 1) {
                return "1 hr ago";
            } else if (diffMinutes > 1) {
                return (int) diffMinutes + " minutes ago";
            } else if (diffMinutes <= 1) {
                return "1 min ago";
            }

        }

        return "";
    }

    public static boolean isNewListing(Long time) {
        // 2 days time
        return time != null && ((Calendar.getInstance().getTime().getTime() - time) < 2 * 24 * 60 * 60 * 1000);
    }
}
