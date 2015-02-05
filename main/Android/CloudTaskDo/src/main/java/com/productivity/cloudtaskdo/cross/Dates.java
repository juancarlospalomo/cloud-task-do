package com.productivity.cloudtaskdo.cross;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JuanCarlos on 30/01/2015.
 */
public final class Dates {

    public static final String DATE_DATABASE_FORMAT = "yyyy-MM-dd HH:mm";

    public static Date getDate(String formattedDate) {
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_DATABASE_FORMAT,
                Locale.getDefault());
        try {
            date = dateFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); //TODO
            date = null;
        }
        return date;
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_DATABASE_FORMAT, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
