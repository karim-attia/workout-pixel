package com.example.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.MainActivity.InteractWithWidget;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */

public class WidgetFunctions extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    public static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WidgetFunctions";


    // Entry point
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "ON_RECEIVE " + intent.getAction() + "\n------------------------------------------------------------------------");

        // CommonFunctions.executorService.execute(() -> {

        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, 0);
            Widget widget = InteractWithWidget.loadWidgetByAppWidgetId(context, appWidgetId);
            widget.updateAfterClick(context);
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            List<Widget> widgetList = InteractWithWidget.loadWidgetsWithValidAppWidgetId(context);
            for (Widget widget : widgetList) {
                widget.updateWidgetBasedOnStatus(context);
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
        List<Widget> widgetList = InteractWithWidget.loadWidgetsWithValidAppWidgetId(context);
        for (Widget widget : widgetList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(TAG, "ON_UPDATE: " + widget.getAppWidgetId());
            widget.updateWidgetBasedOnStatus(context);
        }
        //});
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.v(TAG, "ON_DELETED");
        // Not necessary anymore because I want to keep the deleted ones in the DB

        for (int appWidgetId : appWidgetIds) {
            InteractWithWidget.setAppWidgetIdToNull(context, appWidgetId);
        }
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


