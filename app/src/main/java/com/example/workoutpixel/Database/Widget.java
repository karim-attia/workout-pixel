package com.example.workoutpixel.Database;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.workoutpixel.Core.CommonFunctions;
import com.example.workoutpixel.Core.WidgetFunctions;
import com.example.workoutpixel.MainActivity.ManageSavedPreferences;
import com.example.workoutpixel.PastWorkouts.PastWorkoutsViewModel;
import com.example.workoutpixel.R;

import static com.example.workoutpixel.Core.CommonFunctions.*;
import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getNewStatus;

@Entity(tableName = "widgets")
public class Widget {
    private static final String TAG = "Widget";

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

    public void setAll(int appWidgetId, String title, long lastWorkout, int intervalBlue, int intervalRed, boolean showDate, boolean showTime, String status) {
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.lastWorkout = lastWorkout;
        this.intervalBlue = intervalBlue;
        this.intervalRed = intervalRed;
        this.showDate = showDate;
        this.showTime = showTime;
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return title + ": " + everyWording();
    }

    public String everyWording() {
        String everyWording = "Every " + intervalBlue + " day";
        if (intervalBlue > 1) {
            everyWording += "s";
        }
        return everyWording;
    }

    public String debugString() {
        return appWidgetId + "/ " + title + ": ";
    }

    public void updateAfterClick(Context context) {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "start");
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

        setWidgetText(widgetView);
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green);

        // Instruct the widget manager to update the widget
        runUpdate(context, widgetView);

        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "complete\n------------------------------------------------------------------------");
    }

    public void updateWidgetBasedOnNewStatus(Context context) {
        Log.d(TAG, "updateBasedOnStatus: " + debugString() + "\n------------------------------------------------------------------------");
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);

        // Update the widget in the db (only) when there is a new status.
        if (!status.equals(getNewStatus(lastWorkout, intervalBlue))) {
            setStatus(getNewStatus(lastWorkout, intervalBlue));
            ManageSavedPreferences.updateWidget(context, this);
        }

        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context));
        // Before updating a widget, the text and background of the view need to be set. Otherwise, an old text view
        setWidgetText(widgetView);
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", getDrawableIntFromStatus(status));
        runUpdate(context, widgetView);
    }

    private void setWidgetText(RemoteViews widgetView) {
        widgetView.setTextViewText(R.id.appwidget_text, widgetText());
    }

    // Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
    private void runUpdate(Context context, RemoteViews widgetView) {
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, widgetView);
    }

    // widgetText returns the text of the whole widget based on a Widget object.
    public String widgetText() {
        String widgetText = title;
        if (showDate & !status.equals(STATUS_NONE)) {
            widgetText += "\n" + lastWorkoutDateBeautiful(lastWorkout);
        }
        if (showTime & !status.equals(STATUS_NONE)) {
            widgetText += "\n" + lastWorkoutTimeBeautiful(lastWorkout);
        }
        return widgetText;
    }

    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private PendingIntent widgetPendingIntent(Context context) {
        Intent intent = new Intent(context, WidgetFunctions.class);
        intent.setAction(WidgetFunctions.ACTION_DONE_EXERCISE);
        // put the appWidgetId as an extra to the update intent
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}