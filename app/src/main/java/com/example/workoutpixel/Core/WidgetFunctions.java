package com.example.workoutpixel.Core;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.MainActivity.ManageSavedPreferences;
import com.example.workoutpixel.PastWorkouts.PastWorkoutsViewModel;
import com.example.workoutpixel.R;

import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getNewStatus;
import static com.example.workoutpixel.Core.CommonFunctions.widgetText;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */

public class WidgetFunctions extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    private static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WORKOUT_PIXEL_MAIN";

    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private static PendingIntent widgetPendingIntent(Context context, int intentAppWidgetId) {
        Intent intent = new Intent(context, WidgetFunctions.class);
        intent.setAction(ACTION_DONE_EXERCISE);
        // put the appWidgetId as an extra to the update intent
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intentAppWidgetId);
        return PendingIntent.getBroadcast(context, intentAppWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void updateAfterClick(Context context, Widget widget) {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + widget.getAppWidgetId() + " " + widget.getTitle() + " start");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        long thisWorkoutTime = System.currentTimeMillis();


        // Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
        PastWorkoutsViewModel.insertClickedWorkout(context, widget.getAppWidgetId(), thisWorkoutTime);

        widget.setStatus(STATUS_GREEN);
        widget.setLastWorkout(thisWorkoutTime);
        ManageSavedPreferences.updateWidget(context, widget);

        setWidgetText(widgetView, widget);
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);

        int numberOfPastWorkouts = PastWorkoutsViewModel.getCountOfActiveClickedWorkouts(context, widget.getAppWidgetId());
        Toast.makeText(context, "Oh yeah! Already done this " + numberOfPastWorkouts + " times. :)", Toast.LENGTH_LONG).show();

        // Instruct the widget manager to update the widget
        runUpdate(context, widget.getAppWidgetId(), widgetView);

        Log.d(TAG, "ACTION_DONE_EXERCISE " + widget.getAppWidgetId() + " " + widget.getTitle() + " complete\n------------------------------------------------------------------------");
    }

    public static void updateWidgetBasedOnNewStatus(Context context, Widget widget) {
        Log.d(TAG, "updateBasedOnStatus: " + widget.getAppWidgetId() + " " + widget.getTitle() + "\n------------------------------------------------------------------------");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        widget.setStatus(getNewStatus(widget.getLastWorkout(), widget.getIntervalBlue()));

        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        // Sometimes the onClickListener in the widgets stop working. This also resets the onClickListener every night with the alarm.
        widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context, widget.getAppWidgetId()));
        setWidgetText(widgetView, widget);

        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", CommonFunctions.getDrawableIntFromStatus(widget.getStatus()));
        ManageSavedPreferences.updateWidget(context, widget);
        runUpdate(context, widget.getAppWidgetId(), widgetView);
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

        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, 0);
            Widget widget = ManageSavedPreferences.loadWidgetByAppWidgetId(context, appWidgetId);
            updateAfterClick(context, widget);
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            List<Widget> widgetList = ManageSavedPreferences.loadAllWidgets(context);
            for (Widget widget : widgetList) {
                Log.d(TAG, "ACTION_AUTO_UPDATE for appWidgetId: " + widget.getAppWidgetId());
                updateWidgetBasedOnNewStatus(context, widget);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.v(TAG, "ON_UPDATE\n------------------------------------------------------------------------");
        // Start alarm
        Log.v(TAG, "START_ALARM");
        WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
        widgetAlarm.startAlarm();
        Log.v(TAG, "ALARM_STARTED");

        // TODO: Understand
        // TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not.
        List<Widget> widgetList = ManageSavedPreferences.loadAllWidgets(context);
        for (Widget widget : widgetList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.v(TAG, "ON_UPDATE: " + widget.getAppWidgetId());
            updateWidgetBasedOnNewStatus(context, widget);
        }
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
        if (appWidgetIds(context).length == 0) {
            // stop alarm
            Log.v(TAG, "STOP_ALARM");
            WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
            widgetAlarm.stopAlarm();
            Log.v(TAG, "STOPPED_ALARM");
        }
    }

    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

}


