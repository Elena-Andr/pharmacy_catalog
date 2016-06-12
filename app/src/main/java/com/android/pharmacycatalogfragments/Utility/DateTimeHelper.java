package com.android.pharmacycatalogfragments.Utility;

import android.content.res.Resources;
import android.util.Log;

import com.android.pharmacycatalogfragments.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {
    private static String LOG_TAG = DateTimeHelper.class.getSimpleName();

    public static String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Resources.getSystem().getString(R.string.date_format));
        return dateFormat.format(calendar.getTime());
    }

    public static Calendar getCurrentDateAsCalendar() {
        return Calendar.getInstance();
    }

    public static Calendar getCalendarFromString(String date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d);

        return thatDay;
    }

    public static int getMinutesBetweenDates(Calendar c1, Calendar c2){
        long diff = c1.getTimeInMillis() - c2.getTimeInMillis();
        int minutes = (int) ((diff / (1000*60)) % 60);

        return minutes;
    }

    public static String getStringFromCalendar(Calendar calendar, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(calendar.getTime());
    }
}
