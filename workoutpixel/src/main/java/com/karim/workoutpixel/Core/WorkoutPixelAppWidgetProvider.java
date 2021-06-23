package com.karim.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.karim.workoutpixel.Database.Goal;
import com.karim.workoutpixel.Main.InteractWithGoalInDb;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */

public class WorkoutPixelAppWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    public static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WorkoutPixelAppWidgetProvider";


    // Entry point
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "ON_RECEIVE " + intent.getAction() + "\n------------------------------------------------------------------------");

        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            // TODO: widgetUid -> goalUid
            int uid = intent.getIntExtra("widgetUid", 0);
            Goal goal = InteractWithGoalInDb.loadGoalByUid(context, uid);
            goal.updateAfterClick(context);
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            CommonFunctions.saveTimeWithStringToSharedPreferences(context, "Last Alarm");
            List<Goal> goalList = InteractWithGoalInDb.loadWidgetsWithValidAppWidgetId(context);
            for (Goal goal : goalList) {
                goal.updateWidgetBasedOnStatus(context);
            }
            // TODO: Also update status of goals without widget in DB.
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //CommonFunctions.executorService.execute(() -> {

            // There may be multiple widgets active, so update all of them
        Log.d(TAG, "ON_UPDATE\n------------------------------------------------------------------------");
        // Start alarm
        Log.v(TAG, "START_ALARM");
        WidgetAlarm.startAlarm(context);
        Log.v(TAG, "ALARM_STARTED");

        // TODO: Understand
        // TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
        // TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not,
        List<Goal> goalList = InteractWithGoalInDb.loadWidgetsWithValidAppWidgetId(context);
        for (Goal goal : goalList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(TAG, "ON_UPDATE: " + goal.debugString());
            goal.updateWidgetBasedOnStatus(context);
        }

        // CommonFunctions.executorService.shutdown();

        //});
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.d(TAG, "ON_DELETED");

        // TODO: This is also called if the configuration is aborted. Then, this database call is useless.
        for (int appWidgetId : appWidgetIds) {
            InteractWithGoalInDb.setAppWidgetIdToNullByAppwidgetId(context, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "ON_ENABLED");
        super.onEnabled(context);

        // Start alarm
        Log.v(TAG, "START_ALARM");
        WidgetAlarm.startAlarm(context);
        Log.v(TAG, "ALARM_STARTED");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // Stop alarm only if all widgets have been disabled
        Log.d(TAG, "ON_DISABLED");
        if (CommonFunctions.appWidgetIds(context).length == 0) {
            // stop alarm
            Log.v(TAG, "STOP_ALARM");
            WidgetAlarm.startAlarm(context);
            Log.v(TAG, "STOPPED_ALARM");
        }
    }
}


