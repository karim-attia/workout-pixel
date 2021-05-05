package com.example.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CommonFunctions {
    public static final String STATUS_RED = "RED";
    public static final String STATUS_BLUE = "BLUE";
    public static final String STATUS_GREEN = "GREEN";
    public static final String STATUS_NONE = "NO STATUS";
    public static final int MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "WORKOUT_PIXEL COMMON FUNCTIONS";

    // Controls which status to set. Will make it possible to implement a nicer day switch mechanism in the future.
    public static String getNewStatus(long lastWorkout, int intervalBlue) {

        Calendar today3AmCalendar = Calendar.getInstance();
        today3AmCalendar.setTimeInMillis(System.currentTimeMillis());
        int hourOfDay = today3AmCalendar.get(Calendar.HOUR_OF_DAY);
        today3AmCalendar.set(Calendar.HOUR_OF_DAY, 3);
        today3AmCalendar.set(Calendar.MINUTE, 0);
        long today3Am = today3AmCalendar.getTimeInMillis();
        Log.d(TAG, "intervalBlue: " + intervalBlue);
        Log.d(TAG, "today3Am: " + today3Am);
        Log.d(TAG, "lastWorkout: " + lastWorkout);

        // TODO make this nice somehow. last time 3am?
        long after3Am = 0;
        if (hourOfDay >= 3) {
            after3Am = 1;
        }

        // Point in time when the widget should change to blue/red as soon as it's night time the next time.
        long timeBlue = lastWorkout + intervalBlue - after3Am * MILLISECONDS_IN_A_DAY;
        long timeRed = timeBlue + 2 * MILLISECONDS_IN_A_DAY;

        // Don't change the widget status if this is the first time the alarm runs and thus lastWorkout == 0L.
        if (lastWorkout == 0L) {
            Log.v(TAG, "GetNewStatus: " + STATUS_NONE);
            return STATUS_NONE;
        } else if (timeRed < today3Am) {
            Log.v(TAG, "GetNewStatus: " + STATUS_RED);
            return STATUS_RED;
        } else if (timeBlue < today3Am) {
            Log.v(TAG, "GetNewStatus: " + STATUS_BLUE);
            return STATUS_BLUE;
        }
        Log.v(TAG, "GetNewStatus: " + STATUS_GREEN);
        return STATUS_GREEN;
    }

    public static int getDrawableIntFromStatus(String status) {
        switch (status) {
            case STATUS_GREEN:
                Log.v(TAG, "set to green");
                return R.drawable.rounded_corner_green;
            case STATUS_BLUE:
                Log.v(TAG, "set to blue");
                return R.drawable.rounded_corner_blue;
            case STATUS_RED:
                Log.v(TAG, "set to red");
                return R.drawable.rounded_corner_red;
            case STATUS_NONE:
                Log.v(TAG, "set to purple");
                return R.drawable.rounded_corner_purple;
            default:
                Log.d(TAG, "newStatus not correctly assigned.");
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

    public static String lastWorkoutDateTimeBeautiful(Long longLastWorkout) {
        LocalDateTime lastWorkout = Instant.ofEpochMilli(longLastWorkout).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
        return lastWorkout.format(dateFormatter);
    }

    static int intervalInMilliseconds(int intervalInDays) {
        return intervalInDays * MILLISECONDS_IN_A_DAY;
    }

    static int intervalInDays(int intervalInMilliseconds) {
        return intervalInMilliseconds / MILLISECONDS_IN_A_DAY;
    }

    public static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

    public static List<Widget> widgetsWithoutValidAppWidgetId(Context context, List<Widget> widgets) {
        List<Widget> widgetsWithoutValidAppwidgetId = widgets.stream().filter(widget -> Arrays.stream(appWidgetIds(context)).noneMatch(i -> i == widget.getAppWidgetId())).collect(Collectors.toList());
        Log.d(TAG, "widgetsWithoutValidAppwidgetId");
        Log.d(TAG, "appWidgetIds(context): " + Arrays.toString(appWidgetIds(context)));
        for (Widget widget: widgets) {
            Log.d(TAG, "widgets -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle() + "AppWidgetIds contain it: " + Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId()));
        }
        for (Widget widget: widgetsWithoutValidAppwidgetId) {
            Log.d(TAG, "widgetsWithoutValidAppwidgetId -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle());
        }
        return widgetsWithoutValidAppwidgetId;
    }

    public static List<Widget> widgetsWithValidAppWidgetId(Context context, List<Widget> widgets) {

        List<Widget> widgetsWithValidAppwidgetId = widgets.stream().filter(widget -> Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId())).collect(Collectors.toList());
        Log.d(TAG, "widgetsWithValidAppwidgetId");
        Log.d(TAG, "appWidgetIds(context): " + Arrays.toString(appWidgetIds(context)));
        for (Widget widget: widgets) {
            Log.d(TAG, "widgets -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle() + "AppWidgetIds contain it: " + Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId()));
        }
        for (Widget widget: widgetsWithValidAppwidgetId) {
            Log.d(TAG, "widgetsWithValidAppwidgetId -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle());
        }
        return widgetsWithValidAppwidgetId;
    }

    public static boolean doesWidgetHaveValidAppWidgetId(Context context, Widget widget) {
        return Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId());
    }
}