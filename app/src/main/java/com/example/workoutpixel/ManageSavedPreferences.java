package com.example.workoutpixel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;

// ManageSavedPreferences manages the preferences for every widget. This is either done through the appWidgetId of the widget or through a Widget object.
// Could move them all to the database into a new table at some point
public class ManageSavedPreferences {
    private static final String PREFS_NAME = "com.example.WorkoutPixel";
    private static final String PREF_PREFIX_KEY_TITLE = "appwidget_title_";
    private static final String PREF_PREFIX_KEY_INTERVAL_BLUE = "appwidget_interval_blue";
    private static final String PREF_PREFIX_KEY_INTERVAL_RED = "appwidget_interval_red";
    private static final String PREF_PREFIX_KEY_LAST_WORKOUT = "appwidget_last_workout";
    private static final String PREF_PREFIX_KEY_CURRENT_STATUS = "appwidget_current_status";
    private static final String PREF_PREFIX_KEY_SHOW_DATE = "appwidget_show_date";
    private static final String PREF_PREFIX_KEY_SHOW_TIME = "appwidget_show_time";
    private static final String TAG = "WORKOUT_PIXEL PREFERENCES";

    static void saveAll(Context context, Widget widget) {
        int appWidgetId = widget.getAppWidgetId();
        saveTitle(context, appWidgetId, widget.getTitle());
        saveLastWorkout(context, appWidgetId, widget.getLastWorkout());
        saveIntervalBlue(context, appWidgetId, widget.getIntervalBlue());
        saveIntervalRed(context, appWidgetId, widget.getIntervalRed());
        saveShowDate(context, appWidgetId, widget.getShowDate());
        saveShowTime(context, appWidgetId, widget.getShowTime());
        saveCurrentStatus(context, appWidgetId, widget.getStatus());
    }

    static void saveDuringInitialize(Context context, Widget widget) {
        int appWidgetId = widget.getAppWidgetId();
        saveTitle(context, appWidgetId, widget.getTitle());
        saveIntervalBlue(context, appWidgetId, widget.getIntervalBlue());
        saveIntervalRed(context, appWidgetId, widget.getIntervalRed());
        saveShowDate(context, appWidgetId, widget.getShowDate());
        saveShowTime(context, appWidgetId, widget.getShowTime());
    }

    static void deleteAll(Context context, int appWidgetId) {
        deleteTitle(context, appWidgetId);
        deleteIntervalBlue(context, appWidgetId);
        deleteIntervalRed(context, appWidgetId);
        deleteLastWorkout(context, appWidgetId);
        deleteCurrentStatus(context, appWidgetId);
        deleteShowDate(context, appWidgetId);
        deleteShowTime(context, appWidgetId);
    }

    public static Widget loadWidget(Context context, int appWidgetId) {
        return new Widget(appWidgetId, loadTitle(context, appWidgetId), loadLastWorkout(context, appWidgetId), loadIntervalBlue(context, appWidgetId), loadIntervalRed(context, appWidgetId), loadShowDate(context, appWidgetId), loadShowTime(context, appWidgetId), loadCurrentStatus(context, appWidgetId));
    }

    // TODO: One save/load/delete function with parameters. Second thought: Probably not a great idea because there are strings, ints etc.
    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitle(Context context, int appWidgetId, String text) {
        Log.v(TAG, "saveTitle " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY_TITLE + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static String loadTitle(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY_TITLE + appWidgetId, null);
        if (titleValue != null) {
            // Log.v(TAG, "loadTitle " + appWidgetId + " " + titleValue);
            return titleValue;
        } else {
            // Log.v(TAG, "loadTitle " + appWidgetId + " " + title);
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitle(Context context, int appWidgetId) {
        Log.v(TAG, "deleteTitle " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_TITLE + appWidgetId);
        prefs.apply();
    }

    // INTERVAL BLUE
    static void saveIntervalBlue(Context context, int appWidgetId, Integer Interval) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY_INTERVAL_BLUE + appWidgetId, Interval);
        Log.v(TAG, "saveIntervalBlue Success: " + appWidgetId + " " + Interval);
        prefs.apply();
    }

    static Integer loadIntervalBlue(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "loadIntervalBlue Success: " + appWidgetId + " " + Interval);
        return prefs.getInt(PREF_PREFIX_KEY_INTERVAL_BLUE + appWidgetId, 24*60*60*1000);
    }

    static void deleteIntervalBlue(Context context, int appWidgetId) {
        Log.v(TAG, "deleteIntervalBlue " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_INTERVAL_BLUE + appWidgetId);
        prefs.apply();
    }

    // INTERVAL RED
    static void saveIntervalRed(Context context, int appWidgetId, Integer Interval) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId, Interval);
        Log.v(TAG, "saveIntervalRed Success: " + appWidgetId + " " + Interval);
        prefs.apply();
    }

    static Integer loadIntervalRed(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "loadIntervalRed Success: " + appWidgetId + " " + Interval);
        return prefs.getInt(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId, 2*24*60*60*1000);
    }

    static void deleteIntervalRed(Context context, int appWidgetId) {
        Log.v(TAG, "deleteIntervalRed " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId);
        prefs.apply();
    }


    // LAST WORKOUT
    public static void saveLastWorkout(Context context, int appWidgetId, Long timeLastWorkout) {
        String timeLastWorkoutBeautiful = DateFormat.getDateTimeInstance().format(timeLastWorkout);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY_LAST_WORKOUT + appWidgetId, timeLastWorkout);
        Log.v(TAG, "saveLastWorkout Success: " + appWidgetId + " " + timeLastWorkoutBeautiful +" ");
        prefs.apply();
    }

    static Long loadLastWorkout(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Long timeLastWorkout = prefs.getLong(PREF_PREFIX_KEY_LAST_WORKOUT + appWidgetId, 0L);
        // String timeLastWorkoutBeautiful = DateFormat.getDateTimeInstance().format(timeLastWorkout);
        // Log.v(TAG, "loadLastWorkout Success: " + appWidgetId + " " + timeLastWorkoutBeautiful +" ");
        return timeLastWorkout;
    }

    static void deleteLastWorkout(Context context, int appWidgetId) {
        Log.v(TAG, "deleteLastWorkout " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_LAST_WORKOUT + appWidgetId);
        prefs.apply();
    }

    // CURRENT STATUS
    static void saveCurrentStatus(Context context, int appWidgetId, String currentStatus) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY_CURRENT_STATUS + appWidgetId, currentStatus);
        Log.v(TAG, "saveCurrentStatus Success: " + appWidgetId + " " + currentStatus);
        prefs.apply();
    }

    static String loadCurrentStatus(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "loadCurrentStatus Success: " + appWidgetId + " " + currentStatus);
        return prefs.getString(PREF_PREFIX_KEY_CURRENT_STATUS + appWidgetId, "NO STATUS");
    }

    static void deleteCurrentStatus(Context context, int appWidgetId) {
        Log.v(TAG, "deleteCurrentStatus " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_CURRENT_STATUS + appWidgetId);
        prefs.apply();
    }

    // SHOW TIME AND DATE
    static void saveShowDate(Context context, int appWidgetId, boolean showDate) {
        // Log.v(TAG, "saveShowTimeAndDate Start " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_KEY_SHOW_DATE + appWidgetId, showDate);
        Log.v(TAG, "SAVE_CURRENT_STATUS Success: " + appWidgetId + " " + showDate);
        prefs.apply();
    }

    static void saveShowTime(Context context, int appWidgetId, boolean showTime) {
        // Log.v(TAG, "saveShowTimeAndDate Start " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_KEY_SHOW_TIME + appWidgetId, showTime);
        Log.v(TAG, "SAVE_CURRENT_STATUS Success: " + appWidgetId + " " + showTime);
        prefs.apply();
    }

    static boolean loadShowDate(Context context, int appWidgetId) {
        // Log.v(TAG, "loadShowTimeAndDate Start " + appWidgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "LOAD_CURRENT_STATUS Success: " + appWidgetId + " " + showTimeAndDate);
        return prefs.getBoolean(PREF_PREFIX_KEY_SHOW_DATE + appWidgetId, false);
    }

    static boolean loadShowTime(Context context, int appWidgetId) {
        // Log.v(TAG, "loadShowTimeAndDate Start " + appWidgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "LOAD_CURRENT_STATUS Success: " + appWidgetId + " " + showTimeAndDate);
        return prefs.getBoolean(PREF_PREFIX_KEY_SHOW_TIME + appWidgetId, false);
    }

    static void deleteShowDate(Context context, int appWidgetId) {
        Log.v(TAG, "deleteShowDate " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_SHOW_DATE + appWidgetId);
        prefs.apply();
    }

    static void deleteShowTime(Context context, int appWidgetId) {
        Log.v(TAG, "deleteShowTime " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_SHOW_TIME + appWidgetId);
        prefs.apply();
    }

    // Number of past workouts
    static void saveNumberOfPastWorkouts(Context context, int appWidgetId, Integer newNumber) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId, newNumber);
        Log.v(TAG, "saveIntervalRed Success: " + appWidgetId + " " + newNumber);
        prefs.apply();
    }

    static Integer loadNumberOfPastWorkouts(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Log.v(TAG, "loadIntervalRed Success: " + appWidgetId + " " + Interval);
        return prefs.getInt(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId, 0);
    }

    static void deletNumberOfPastWorkouts(Context context, int appWidgetId) {
        Log.v(TAG, "deleteIntervalRed " + appWidgetId);
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_INTERVAL_RED + appWidgetId);
        prefs.apply();
    }

    static void increaseNumberOfPastWorkouts(Context context, int appWidgetId) {
        saveNumberOfPastWorkouts(context, appWidgetId, loadNumberOfPastWorkouts(context, appWidgetId)+1);
    }
}
