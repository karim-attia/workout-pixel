package com.example.workoutpixel.Core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

// Responsible to start and stop the alarm that updates the widget at 3:00 every day.
// The alarm is started when the first widget is created (by WorkoutPixel.onCreate()) or the device restarts (by WorkoutPixel.onUpdate()).
// WorkoutPixel.onUpdate() may not only be called upon device restarts but also in other cases. Check the documentation for that.
public class WidgetAlarm {
    private static final String TAG = "WORKOUT_PIXEL ALARM";
    private final int MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;
    private final Context mContext;

    // Make singleton?
    public WidgetAlarm(Context context) {
        mContext = context;
    }

    public void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        //calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.MILLISECOND, 0);
        if (hourOfDay >= 3) {
            // Can be commented to test alarm -> it will come up more or less instantly after restart.
            calendar.setTimeInMillis(calendar.getTimeInMillis() + MILLISECONDS_IN_A_DAY);
        }

        Log.d(TAG, "STARTING_ALARM " + dateTimeString(calendar.getTimeInMillis()));

        // RTC does not wake the device up
        alarmManager().setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent());
    }

    public void stopAlarm() {
        Log.v(TAG, "STOPPING_ALARM");
        alarmManager().cancel(pendingIntent());
    }

    // Just for log. For anything else, use the functions in CommonFunctions
    private String dateTimeString(long timeInMillis) {
        return DateFormat.getDateTimeInstance().format(timeInMillis);
    }

    private AlarmManager alarmManager() {
        return (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent pendingIntent() {
        Intent alarmIntent = new Intent(mContext, WidgetFunctions.class);
        alarmIntent.setAction(WidgetFunctions.ACTION_ALARM_UPDATE);
        return PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}