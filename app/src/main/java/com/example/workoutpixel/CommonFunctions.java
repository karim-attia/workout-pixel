package com.example.workoutpixel;

import android.graphics.Color;
import android.util.Log;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Locale;

public class CommonFunctions {
    private static final String TAG = "WORKOUT_PIXEL WORKOUT STATUS";
    public static final String STATUS_RED = "RED";
    public static final String STATUS_BLUE = "BLUE";
    public static final String STATUS_GREEN = "GREEN";
    public static final String STATUS_NONE = "NO STATUS";
    public static final int MILLISECONDS_IN_A_DAY = 24*60*60*1000;


    // Controls which status to set. Will make it possible to implement a nicer day switch mechanism in the future.
    public static String getNewStatus(long lastWorkout, int interval){

        Calendar today3Am = Calendar.getInstance();
        today3Am.setTimeInMillis(System.currentTimeMillis());
        today3Am.set(Calendar.HOUR_OF_DAY, 3);
        today3Am.set(Calendar.MINUTE, 0);

        // Point in time when the widget should change to blue/red as soon as it's night time the next time.
        long timeBlue = lastWorkout + interval - MILLISECONDS_IN_A_DAY;
        long timeRed = timeBlue + 2*MILLISECONDS_IN_A_DAY;

        // Don't change the widget status if this is the first time the alarm runs and thus oldStatus == STATUS_NONE.
        if (lastWorkout == 0L) {
            Log.v(TAG, "GetNewStatus: " + STATUS_NONE);
            return STATUS_NONE;
        }
        else if (timeRed < today3Am.getTimeInMillis()) {
            Log.v(TAG, "GetNewStatus: " + STATUS_RED);
            return STATUS_RED;
        }
        else if (timeBlue < today3Am.getTimeInMillis()) {
            Log.v(TAG, "GetNewStatus: " + STATUS_BLUE);
            return STATUS_BLUE;
        };
        Log.v(TAG, "GetNewStatus: " + STATUS_GREEN);
        return STATUS_GREEN;
    }

    public static int getDrawableIntFromStatus(String status){
        switch (status) {
            case STATUS_GREEN:
                return R.drawable.rounded_corner_green;
            case STATUS_BLUE:
                return R.drawable.rounded_corner_blue;
            case STATUS_RED:
                return R.drawable.rounded_corner_red;
            case STATUS_NONE:
                return R.drawable.rounded_corner_purple;
        }
        return 0;
    }

    public static int getColorFromStatus(String status){
        switch (status) {
            case STATUS_GREEN:
                return Color.parseColor("#388e3c");
            case STATUS_BLUE:
                return Color.parseColor("#1976d2");
            case STATUS_RED:
                return Color.parseColor("#d32f2f");
            case STATUS_NONE:
                return Color.parseColor("#7b1fa2");
        }
        return 0;
    }

    // widgetText returns the text of the whole widget based on a Widget object.
    public static String widgetText(Widget widget) {
        String widgetText = widget.getTitle();
        if (widget.getShowDate() & !widget.getStatus().equals(STATUS_NONE)) {
            widgetText += "\n" + lastWorkoutDateBeautiful(widget.getLastWorkout());
        }
        if (widget.getShowTime() & !widget.getStatus().equals(STATUS_NONE)) {
            widgetText += "\n" + lastWorkoutTimeBeautiful(widget.getLastWorkout());
        }
        return widgetText;
    }

    public static String lastWorkoutDateBeautiful(Long longLastWorkout) {
        LocalDateTime lastWorkout = Instant.ofEpochMilli(longLastWorkout).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
        return lastWorkout.format(dateFormatter);
    }
    public static String lastWorkoutTimeBeautiful(Long longLastWorkout) {
        LocalDateTime lastWorkout = Instant.ofEpochMilli(longLastWorkout).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
        return lastWorkout.format(dateFormatter);
    }

}