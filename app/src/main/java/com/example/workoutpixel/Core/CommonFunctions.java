package com.example.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Main.InteractWithGoalInDb;
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
    private static final String TAG = "WORKOUT_PIXEL COMMON FUNCTIONS";
    // TODO: Change to enum or int 0-3. enum not great for room DB.
    public static final String STATUS_NONE = "NO STATUS";
    public static final String STATUS_GREEN = "GREEN";
    public static final String STATUS_BLUE = "BLUE";
    public static final String STATUS_RED = "RED";
    public static final int MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;
    private static final String PREFS_NAME = "com.example.WorkoutPixel";
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // private static final int NUMBER_OF_THREADS = 4;
    // public static final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Get new status
     */
    // Controls which status to set.
    public static String getNewStatus(long lastWorkout, int intervalBlue) {

        // Point in time when the widget should change to blue/red as soon as it's night time the next time.
        long timeBlue = lastWorkout + intervalInMilliseconds(intervalBlue -1);
        long timeRed = timeBlue + intervalInMilliseconds(2);

        // Don't change the widget status if this is the first time the alarm runs and thus lastWorkout == 0L.
        if (lastWorkout == 0L) {
            // Log.d(TAG, "GetNewStatus: " + STATUS_NONE);
            return STATUS_NONE;
        } else if (timeRed < last3Am()) {
            // Log.d(TAG, "GetNewStatus: " + STATUS_RED);
            return STATUS_RED;
        } else if (timeBlue < last3Am()) {
            // Log.d(TAG, "GetNewStatus: " + STATUS_BLUE);
            return STATUS_BLUE;
        }
        // Log.d(TAG, "GetNewStatus: " + STATUS_GREEN);
        return STATUS_GREEN;
    }

    /**
     * Calendar stuff
     */
    public static long last3Am() {
        long next3Am = today3Am();
        // If it's before 3am, the last 3am way yesterday, thus subtract a day.
        if (!after3Am()) {
            // Can be commented to test alarm -> it will come up more or less instantly after restart.
            next3Am -= CommonFunctions.intervalInMilliseconds(1);
        }
        return next3Am;
    }

    public static long next3Am() {
        long next3Am = today3Am();
        // If it's already after 3am, the next 3am will be tomorrow, thus add a day.
        if (after3Am()) {
            // Can be commented to test alarm -> it will come up more or less instantly after restart.
            next3Am += CommonFunctions.intervalInMilliseconds(1);
        }
        return next3Am;
    }

    public static long today3Am() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean after3Am() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR_OF_DAY) > 3;
    }

    public static long intervalInMilliseconds(int intervalInDays) {
        return (long) MILLISECONDS_IN_A_DAY * intervalInDays;
    }

    public static int intervalInDays(long intervalInMilliseconds) {
        return (int) (intervalInMilliseconds / MILLISECONDS_IN_A_DAY);
    }

    /**
     * Match status to background color
     */
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

    /**
     * Time and date formatting stuff
     */
    public static String dateBeautiful(Long longLastWorkout) {
        if (longLastWorkout == 0L) {return "Never";} else {
            LocalDateTime lastWorkout = Instant.ofEpochMilli(longLastWorkout).atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
            return lastWorkout.format(dateFormatter);
        }
    }

    public static String timeBeautiful(Long time) {
        LocalDateTime lastWorkout = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
        return lastWorkout.format(dateFormatter);
    }

    public static String dateTimeBeautiful(Long time) {
        LocalDateTime lastWorkout = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(new Locale("de", "CH"));
        return lastWorkout.format(dateFormatter);
    }

    /**
     * Save to shared preferences stuff
     */
    // Last alarm
    public static void saveTimeWithStringToSharedPreferences(Context context, String string) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String timeLastWorkoutBeautiful = CommonFunctions.dateTimeBeautiful(System.currentTimeMillis());
        prefs.putString(string, timeLastWorkoutBeautiful);
        prefs.apply();
    }

    /**
     * AppWidgetId stuff
     */
    public static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WorkoutPixelAppWidgetProvider.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

    public static List<Goal> goalsWithoutValidAppWidgetId(Context context, List<Goal> goals) {
        List<Goal> widgetsWithoutValidAppwidgetId = goals.stream().filter(widget -> Arrays.stream(appWidgetIds(context)).noneMatch(i -> widget.getAppWidgetId() == null || i == widget.getAppWidgetId())).collect(Collectors.toList());
/*
        Log.d(TAG, "widgetsWithoutValidAppwidgetId");
        Log.d(TAG, "appWidgetIds(context): " + Arrays.toString(appWidgetIds(context)));
        for (Widget widget: widgets) {
            Log.d(TAG, "widgets -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle() + "AppWidgetIds contain it: " + Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId()));
        }
        for (Widget widget: widgetsWithoutValidAppwidgetId) {
            Log.d(TAG, "widgetsWithoutValidAppwidgetId -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle());
        }
*/
        return widgetsWithoutValidAppwidgetId;
    }

    // Not needed anymore if appWidgetId is set to null on deletion. Could be used to check consistency.
    public static List<Goal> widgetsWithValidAppWidgetId(Context context, List<Goal> goals) {

        List<Goal> widgetsWithValidAppwidgetId = goals.stream().filter(widget -> Arrays.stream(appWidgetIds(context)).anyMatch(i -> !(widget.getAppWidgetId() == null) && i == widget.getAppWidgetId())).collect(Collectors.toList());
/*
        Log.d(TAG, "widgetsWithValidAppwidgetId");
        Log.d(TAG, "appWidgetIds(context): " + Arrays.toString(appWidgetIds(context)));
        for (Widget widget: widgets) {
            Log.d(TAG, "widgets -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle() + "AppWidgetIds contain it: " + Arrays.stream(appWidgetIds(context)).anyMatch(i -> i == widget.getAppWidgetId()));
        }
        for (Widget widget: widgetsWithValidAppwidgetId) {
            Log.d(TAG, "widgetsWithValidAppwidgetId -> AppWidgetId: " + widget.getAppWidgetId() + " Title: " + widget.getTitle());
        }
*/
        return widgetsWithValidAppwidgetId;
    }

    // Not needed anymore if appWidgetId is set to null on deletion. Could be used to check consistency.
    public static boolean doesWidgetHaveValidAppWidgetId(Context context, Goal goal) {
        boolean doesWidgetHaveValidAppWidgetId = Arrays.stream(appWidgetIds(context)).anyMatch(i -> goal.getAppWidgetId() == null || i == goal.getAppWidgetId());
        Log.d(TAG, "doesWidgetHaveValidAppWidgetId " + goal.debugString() + doesWidgetHaveValidAppWidgetId);
        return doesWidgetHaveValidAppWidgetId;
    }

    // Sets all appWidgetIds of goals that are not valid to null. Maybe later even reassigns some to unassigned widgets.
    public static void cleanGoals(Context context, List<Goal> goals) {
        for (Goal goal: goalsWithoutValidAppWidgetId(context, goals)) {
            if(goal.hasValidAppWidgetId()) {
                InteractWithGoalInDb.setAppWidgetIdToNullByUid(context, goal.getUid());
            }
        }
    }
}