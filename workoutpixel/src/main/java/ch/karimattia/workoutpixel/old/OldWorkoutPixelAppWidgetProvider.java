package ch.karimattia.workoutpixel.old;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import ch.karimattia.workoutpixel.activities.ConfigureActivity;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */

@AndroidEntryPoint
public class OldWorkoutPixelAppWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    public static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WorkoutPixelAppWidgetProvider";

    @Inject
    GoalSaveActions.Factory goalSaveActionsFactory;
    private GoalSaveActions goalSaveActions(Goal goal) {
        return goalSaveActionsFactory.create(goal);
    }
    @Inject
    WidgetAlarm widgetAlarm;

    // Entry point
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "ON_RECEIVE " + intent.getAction() + "\n------------------------------------------------------------------------");

        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            int uid = intent.getIntExtra("goalUid", 0);
            Goal goal = OldGoalViewModel.loadGoalByUid(context, uid);
            goalSaveActions(goal).updateAfterClick();
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            OldCommonFunctions.saveTimeWithStringToSharedPreferences(context, "Last ACTION_ALARM_UPDATE " + OldCommonFunctions.dateTimeBeautiful(System.currentTimeMillis()));
            List<Goal> goalList = OldGoalViewModel.loadAllGoals(context);
            for (Goal goal : goalList) {
                goalSaveActions(goal).updateWidgetBasedOnStatus();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //CommonFunctions.executorService.execute(() -> {

        Log.d(TAG, "ON_UPDATE\n------------------------------------------------------------------------");
        // Start alarm
        widgetAlarm.startAlarm();

        // TODO: Understand
        // TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
        // TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not.
        List<OldGoal> goalList = OldGoalViewModel.loadGoalsWithValidAppWidgetId(context);
        for (OldGoal goal : goalList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(TAG, "ON_UPDATE: " + goal.debugString());
            goalSaveActions(goal).updateWidgetBasedOnStatus();
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
            OldGoalViewModel.setAppWidgetIdToNullByAppwidgetId(context, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "ON_ENABLED");
        super.onEnabled(context);

        List<Goal> goalList = OldGoalViewModel.loadGoalsWithValidAppWidgetId(context);
        for (Goal goal : goalList) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.d(TAG, "ON_ENABLED: " + goal.debugString());
            goalSaveActions(goal).updateWidgetBasedOnStatus();
        }

        // Start alarm
        Log.v(TAG, "START_ALARM");
        widgetAlarm.startAlarm();
        Log.v(TAG, "ALARM_STARTED");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        // Stop alarm only if all widgets have been disabled
        Log.d(TAG, "ON_DISABLED");
        if (OldCommonFunctions.appWidgetIds(context).length == 0) {
            // stop alarm
            Log.v(TAG, "STOP_ALARM");
            widgetAlarm.stopAlarm();
            Log.v(TAG, "STOPPED_ALARM");
        }
    }

    // Entry point
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle bundle) {
        Goal goal = OldGoalViewModel.loadGoalByAppWidgetId(context, appWidgetId);
        new GoalWidgetActions(context, goal).runUpdate(false);
    }
}


