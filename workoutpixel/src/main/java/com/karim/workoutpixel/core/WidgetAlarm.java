package com.karim.workoutpixel.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;

import static com.karim.workoutpixel.core.CommonFunctions.next3Am;
import static com.karim.workoutpixel.core.CommonFunctions.saveTimeWithStringToSharedPreferences;

// Responsible to start and stop the alarm that updates the widget at 3:00 every day.
// The alarm is started when the first widget is created (by WorkoutPixel.onCreate()) or the device restarts (by WorkoutPixel.onUpdate()).
// WorkoutPixel.onUpdate() may not only be called upon device restarts but also in other cases. Check the documentation for that.
public class WidgetAlarm {
    private static final String TAG = "WORKOUT_PIXEL ALARM";

    public static void startAlarm(Context context) {
        Log.d(TAG, "STARTING_ALARM " + dateTimeString(next3Am()));
        saveTimeWithStringToSharedPreferences(context, "WidgetAlarm startAlarm");

        // RTC does not wake the device up
        alarmManager(context).setInexactRepeating(AlarmManager.RTC, next3Am(), AlarmManager.INTERVAL_DAY, pendingIntent(context));
    }

    public static void stopAlarm(Context context) {
        Log.d(TAG, "STOPPING_ALARM");
        saveTimeWithStringToSharedPreferences(context, "WidgetAlarm stopAlarm");
        alarmManager(context).cancel(pendingIntent(context));
    }

    // Just for log. For anything else, use the functions in CommonFunctions
    private static String dateTimeString(long timeInMillis) {
        return DateFormat.getDateTimeInstance().format(timeInMillis);
    }

    private static AlarmManager alarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent pendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, WorkoutPixelAppWidgetProvider.class);
        alarmIntent.setAction(WorkoutPixelAppWidgetProvider.ACTION_ALARM_UPDATE);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}