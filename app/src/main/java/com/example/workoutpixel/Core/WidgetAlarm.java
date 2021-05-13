package com.example.workoutpixel.Core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;

import static com.example.workoutpixel.Core.CommonFunctions.next3am;
import static com.example.workoutpixel.Core.CommonFunctions.saveTimeWithStringToSharedPreferences;

// Responsible to start and stop the alarm that updates the widget at 3:00 every day.
// The alarm is started when the first widget is created (by WorkoutPixel.onCreate()) or the device restarts (by WorkoutPixel.onUpdate()).
// WorkoutPixel.onUpdate() may not only be called upon device restarts but also in other cases. Check the documentation for that.
public class WidgetAlarm {
    private static final String TAG = "WORKOUT_PIXEL ALARM";
    private final Context context;

    // Make singleton?
    public WidgetAlarm(Context context) {
        this.context = context;
        saveTimeWithStringToSharedPreferences(context, "WidgetAlarm Constructor");
    }

    public void startAlarm() {
        Log.d(TAG, "STARTING_ALARM " + dateTimeString(next3am()));
        saveTimeWithStringToSharedPreferences(context, "WidgetAlarm startAlarm");

        // RTC does not wake the device up
        alarmManager().setInexactRepeating(AlarmManager.RTC, next3am(), AlarmManager.INTERVAL_DAY, pendingIntent());
    }

    public void stopAlarm() {
        Log.d(TAG, "STOPPING_ALARM");
        saveTimeWithStringToSharedPreferences(context, "WidgetAlarm stopAlarm");
        alarmManager().cancel(pendingIntent());
    }

    // Just for log. For anything else, use the functions in CommonFunctions
    private String dateTimeString(long timeInMillis) {
        return DateFormat.getDateTimeInstance().format(timeInMillis);
    }

    public AlarmManager alarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent pendingIntent() {
        Intent alarmIntent = new Intent(context, WorkoutPixelAppWidgetProvider.class);
        alarmIntent.setAction(WorkoutPixelAppWidgetProvider.ACTION_ALARM_UPDATE);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}