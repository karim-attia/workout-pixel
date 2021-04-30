package com.example.workoutpixel;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.workoutpixel.ClickedWorkouts.ClickedWorkoutViewModel;

import static com.example.workoutpixel.CommonFunctions.STATUS_BLUE;
import static com.example.workoutpixel.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.CommonFunctions.STATUS_NONE;
import static com.example.workoutpixel.CommonFunctions.STATUS_RED;
import static com.example.workoutpixel.CommonFunctions.getNewStatus;
import static com.example.workoutpixel.CommonFunctions.widgetText;
import static com.example.workoutpixel.ManageSavedPreferences.deleteAll;
import static com.example.workoutpixel.ManageSavedPreferences.increaseNumberOfPastWorkouts;
import static com.example.workoutpixel.ManageSavedPreferences.loadWidget;
import static com.example.workoutpixel.ManageSavedPreferences.saveCurrentStatus;
import static com.example.workoutpixel.ManageSavedPreferences.saveLastWorkout;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity WorkoutPixelConfigureActivity}
 */
public class WidgetFunctions extends AppWidgetProvider {

    public static final String ACTION_ALARM_UPDATE = "ALARM_UPDATE";
    // TODO: Replace strings with enums?
    private static final String ACTION_DONE_EXERCISE = "DONE_EXERCISE";
    private static final String TAG = "WORKOUT_PIXEL_MAIN";

    // Updates the widget from the configuration activity.
    static void updateAppWidget(Context context, int appWidgetId, boolean isNew) {
        Log.v(TAG, "UPDATE_APP_WIDGET. appWidgetId: " + appWidgetId);
        // Also executed after phone restart
        initiateBasedOnStatus(context, appWidgetId);
        if (isNew) {
            Toast.makeText(context, "Widget created. Click on it to register a workout.", Toast.LENGTH_LONG).show();
        } else if (!isNew) {
            Toast.makeText(context, "Widget updated.", Toast.LENGTH_LONG).show();
        }
    }

    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private static PendingIntent widgetPendingIntent(Context context, int intentAppWidgetId) {
        Intent intent = new Intent(context, WidgetFunctions.class);
        intent.setAction(ACTION_DONE_EXERCISE);
        // put the appWidgetId as an extra to the update intent
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intentAppWidgetId);
        return PendingIntent.getBroadcast(context, intentAppWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void updateAfterClick(Context context, int appWidgetId) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);


        // This receives the appWidgetId
        long thisWorkoutTime = System.currentTimeMillis();

        Log.d(TAG, "ACTION_DONE_EXERCISE " + appWidgetId + " start");
        // TODO: reflect deletion (=inactivation) of a workout in the count. Or just use count of active in DB.
        increaseNumberOfPastWorkouts(context, appWidgetId);

        // Add the clicked workout to the database. Technicalities are taken care of in InteractWithClickedWorkouts.
        ClickedWorkoutViewModel.insertClickedWorkout(context, appWidgetId, thisWorkoutTime);

        Widget widget = loadWidget(context, appWidgetId);
        widget.setStatus(STATUS_GREEN);
        widget.setLastWorkout(thisWorkoutTime);
        // saveAll(context, widget);
        saveCurrentStatus(context, appWidgetId, STATUS_GREEN);
        saveLastWorkout(context, appWidgetId, thisWorkoutTime);

        setWidgetText(widgetView, widget);
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);

        ClickedWorkoutViewModel.executorService.execute(() -> {
            int numberOfPastWorkouts = ClickedWorkoutViewModel.getCountOfActiveClickedWorkouts(context, appWidgetId);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                // toast(context, numberOfPastWorkouts);
                Toast.makeText(context, "Oh yeah! Already done this " + numberOfPastWorkouts + " times. :)", Toast.LENGTH_LONG).show();
            });
        });

        // Instruct the widget manager to update the widget
        runUpdate(context, appWidgetId, widgetView);
        Log.v(TAG, "ACTION_DONE_EXERCISE " + appWidgetId + " complete\n------------------------------------------------------------------------");
    }

    public static void updateBasedOnNewStatus(Context context, int appWidgetId) {
        Log.v(TAG, "updateBasedOnStatus: " + appWidgetId + "\n------------------------------------------------------------------------");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        Widget widget = loadWidget(context, appWidgetId);
        String newStatus = getNewStatus(widget.getLastWorkout(), widget.getIntervalBlue());
        widget.setStatus(newStatus);

        setWidgetText(widgetView, widget);

        switch (newStatus) {
            case STATUS_BLUE:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_blue);
                saveCurrentStatus(context, appWidgetId, newStatus);
                // runUpdate inside if statement, because if the status is green, it may update to a random last used color.
                runUpdate(context, appWidgetId, widgetView);
                Log.v(TAG, "set to blue");
                break;
            case STATUS_RED:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_red);
                saveCurrentStatus(context, appWidgetId, newStatus);
                // runUpdate inside if statement, because if the status is green, it may update to a random last used color.
                runUpdate(context, appWidgetId, widgetView);
                Log.v(TAG, "set to red");
                break;
            case STATUS_GREEN:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);
                saveCurrentStatus(context, appWidgetId, newStatus);
                // runUpdate inside if statement, because if the status is green, it may update to a random last used color.
                runUpdate(context, appWidgetId, widgetView);
                Log.v(TAG, "set to red");
                break;
            case STATUS_NONE:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_purple);
                saveCurrentStatus(context, appWidgetId, newStatus);
                // runUpdate inside if statement, because if the status is green, it may update to a random last used color.
                runUpdate(context, appWidgetId, widgetView);
                Log.v(TAG, "set to purple");
                break;
        }
    }

    // TODO: Combine again with updateBasedOnNewStatus so that it also updates the widgets based on the status
    public static void initiateBasedOnStatus(Context context, int appWidgetId) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        Widget widget = loadWidget(context, appWidgetId);

        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context, appWidgetId));
        setWidgetText(widgetView, widget);
        Log.v(TAG, "set widget text");

        switch (widget.getStatus()) {
            case STATUS_GREEN:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);
                Log.v(TAG, "initiated green");
                break;
            case STATUS_BLUE:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_blue);
                Log.v(TAG, "initiated blue");
                break;
            case STATUS_RED:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_red);
                Log.v(TAG, "initiated red");
                break;
            case STATUS_NONE:
                widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_purple);
                Log.v(TAG, "initiated purple");
                break;
        }
        runUpdate(context, appWidgetId, widgetView);
    }

    private static void setWidgetText(RemoteViews widgetView, Widget widget) {
        widgetView.setTextViewText(R.id.appwidget_text, widgetText(widget));
    }

    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

    // Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
    private static void runUpdate(Context context, int appWidgetId, RemoteViews widgetView) {
        // final Context context1 = WorkoutPixelConfigureActivity.this;
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, widgetView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "ON_RECEIVE " + intent.getAction() + "\n------------------------------------------------------------------------");

        // Do this if the widget has been clicked
        if (ACTION_DONE_EXERCISE.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, 0);
            updateAfterClick(context, appWidgetId);
        }

        // Do this when the alarm hits
        if (ACTION_ALARM_UPDATE.equals(intent.getAction())) {
            for (int appWidgetId : appWidgetIds(context)) {
                Log.d(TAG, "ACTION_AUTO_UPDATE for appWidgetId: " + appWidgetId);
                updateBasedOnNewStatus(context, appWidgetId);
                // Sometimes the onClickListener in the widgets stop working. This resets the onClickListener every night with the alarm.
                initiateBasedOnStatus(context, appWidgetId);
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

        for (int appWidgetId : appWidgetIds) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            Log.v(TAG, "ON_UPDATE: " + appWidgetId);
            initiateBasedOnStatus(context, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        Log.v(TAG, "ON_DELETED");
        for (int appWidgetId : appWidgetIds) {
            deleteAll(context, appWidgetId);
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
        if (appWidgetIds(context).length == 0) {
            // stop alarm
            Log.v(TAG, "STOP_ALARM");
            WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
            widgetAlarm.stopAlarm();
            Log.v(TAG, "STOPPED_ALARM");
        }
    }
}


