package com.example.workoutpixel.Core;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.workoutpixel.MainActivity.ManageSavedPreferences;
import com.example.workoutpixel.PastWorkouts.PastWorkoutsViewModel;
import com.example.workoutpixel.R;

import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getNewStatus;
import static com.example.workoutpixel.Core.CommonFunctions.widgetText;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */

@Entity(tableName = "widgets")
public class Widget extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    private static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WORKOUT_PIXEL_MAIN";

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "appWidgetId")
    private int appWidgetId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "lastWorkout")
    private long lastWorkout;
    @ColumnInfo(name = "intervalBlue")
    private int intervalBlue;
    @ColumnInfo(name = "intervalRed")
    private int intervalRed;
    @ColumnInfo(name = "showDate")
    private boolean showDate;
    @ColumnInfo(name = "showTime")
    private boolean showTime;
    @ColumnInfo(name = "status")
    private String status;

    public Widget(Integer appWidgetId, String title, long lastWorkout, int intervalBlue, int intervalRed, boolean showDate, boolean showTime, String status) {
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.lastWorkout = lastWorkout;
        this.intervalBlue = intervalBlue;
        this.intervalRed = intervalRed;
        this.showDate = showDate;
        this.showTime = showTime;
        this.status = status;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }
    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }
    public String getTitle() {
        if (title != null) {
            return title;
        } else {
            return "";
        }
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getLastWorkout() {
        return lastWorkout;
    }
    public void setLastWorkout(long lastWorkout) {
        this.lastWorkout = lastWorkout;
    }
    public int getIntervalBlue() {
        return intervalBlue;
    }
    public void setIntervalBlue(int intervalBlue) {
        this.intervalBlue = intervalBlue;
    }
    public int getIntervalRed() {
        return intervalRed;
    }
    public void setIntervalRed(int intervalRed) {
        this.intervalRed = intervalRed;
    }
    public boolean getShowDate() {
        return showDate;
    }
    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }
    public boolean getShowTime() {
        return showTime;
    }
    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }
    public String getStatus() {
        if (status != null) {
            return status;
        } else {
            return "";
        }
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }


    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private PendingIntent widgetPendingIntent(Context context) {
        Intent intent = new Intent(context, this.getClass());
        intent.setAction(ACTION_DONE_EXERCISE);
        // put the appWidgetId as an extra to the update intent
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        Log.d(TAG, "set widgetPendingIntent for " + appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void updateAfterClick(Context context) {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + appWidgetId + " " + title + " start");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        long thisWorkoutTime = System.currentTimeMillis();

        setStatus(STATUS_GREEN);
        setLastWorkout(thisWorkoutTime);
        ManageSavedPreferences.updateWidget(context, this);

        int numberOfPastWorkouts = PastWorkoutsViewModel.getCountOfActiveClickedWorkouts(context, uid) + 1;
        // Handler handler = new Handler(Looper.getMainLooper());
        // handler.post(() ->
        Toast.makeText(context, "Oh yeah! Already done this " + numberOfPastWorkouts + " times. :)", Toast.LENGTH_LONG).show();

        // Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
        PastWorkoutsViewModel.insertClickedWorkout(context, uid, thisWorkoutTime);

        setWidgetText(widgetView, this);
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);

        // Instruct the widget manager to update the widget
        runUpdate(context, appWidgetId, widgetView);

        Log.d(TAG, "ACTION_DONE_EXERCISE " + appWidgetId + " " + title + " complete\n------------------------------------------------------------------------");
    }

    public void updateWidgetBasedOnNewStatus(Context context) {
        Log.d(TAG, "updateBasedOnStatus: " + appWidgetId + " " + title + "\n------------------------------------------------------------------------");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        // Update the widget in the db (only) when there is a new status.
        if (!status.equals(getNewStatus(lastWorkout, intervalBlue))) {
            setStatus(getNewStatus(lastWorkout, intervalBlue));
            ManageSavedPreferences.updateWidget(context, this);
        }

        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        // Sometimes the onClickListener in the widgets stop working. This also resets the onClickListener every night with the alarm.
        widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context));
        setWidgetText(widgetView, this);

        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", CommonFunctions.getDrawableIntFromStatus(status));
        runUpdate(context, appWidgetId, widgetView);
        Log.d(TAG, "ran update");
    }

    private static void setWidgetText(RemoteViews widgetView, Widget widget) {
        widgetView.setTextViewText(R.id.appwidget_text, widgetText(widget));
    }

    // Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
    private static void runUpdate(Context context, int appWidgetId, RemoteViews widgetView) {
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, widgetView);
    }

    // Entry point
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "ON_RECEIVE " + intent.getAction() + "\n------------------------------------------------------------------------");

        // CommonFunctions.executorService.execute(() -> {


        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, 0);
            // TODO is it possible to populate "this"?
            Widget widget = ManageSavedPreferences.loadWidgetByAppWidgetId(context, appWidgetId);
            widget.updateAfterClick(context);
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            List<Widget> widgetList = CommonFunctions.widgetsWithValidAppWidgetId(context, ManageSavedPreferences.loadAllWidgets(context));
            for (Widget widget : widgetList) {
                Log.d(TAG, "ACTION_AUTO_UPDATE for appWidgetId: " + widget.getAppWidgetId());
                updateWidgetBasedOnNewStatus(context);
            }
        }

        // });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //CommonFunctions.executorService.execute(() -> {

        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "ON_UPDATE\n------------------------------------------------------------------------");
        // Start alarm
        Log.d(TAG, "START_ALARM");
        WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
        widgetAlarm.startAlarm();
        Log.d(TAG, "ALARM_STARTED");

        // TODO: Understand
        // TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
        List<Widget> widgetList = CommonFunctions.widgetsWithValidAppWidgetId(context, ManageSavedPreferences.loadAllWidgets(context));
        for (Widget widget : widgetList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(TAG, "ON_UPDATE: " + widget.getAppWidgetId());
            widget.updateWidgetBasedOnNewStatus(context);
        }
        //});
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.v(TAG, "ON_DELETED");
        // Not necessary anymore because I want to keep the deleted ones in the DB
/*
        for (int appWidgetId : appWidgetIds) {
            ManageSavedPreferences.deleteAll(context, appWidgetId);
        }
*/
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.v(TAG, "ON_ENABLED");
        super.onEnabled(context);

        // Start alarm
        Log.v(TAG, "START_ALARM");
        WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
        widgetAlarm.startAlarm();
        Log.v(TAG, "ALARM_STARTED");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // Stop alarm only if all widgets have been disabled
        Log.v(TAG, "ON_DISABLED");
        if (CommonFunctions.appWidgetIds(context).length == 0) {
            // stop alarm
            Log.v(TAG, "STOP_ALARM");
            WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
            widgetAlarm.stopAlarm();
            Log.v(TAG, "STOPPED_ALARM");
        }
    }


}


