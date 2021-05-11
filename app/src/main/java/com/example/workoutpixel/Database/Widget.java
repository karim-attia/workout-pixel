package com.example.workoutpixel.Database;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.workoutpixel.Core.WidgetFunctions;
import com.example.workoutpixel.Main.InteractWithWidgetInDb;
import com.example.workoutpixel.PastWorkouts.InteractWithPastWorkout;
import com.example.workoutpixel.R;

import static com.example.workoutpixel.Core.CommonFunctions.*;
import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getNewStatus;

@Entity(tableName = "widgets")
public class Widget {
    private static final String TAG = "Widget";

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @Nullable
    @ColumnInfo(name = "appWidgetId")
    private Integer appWidgetId;
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

    public Widget(@Nullable Integer appWidgetId, String title, long lastWorkout, int intervalBlue, int intervalRed, boolean showDate, boolean showTime, String status) {
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.lastWorkout = lastWorkout;
        this.intervalBlue = intervalBlue;
        this.intervalRed = intervalRed;
        this.showDate = showDate;
        this.showTime = showTime;
        this.status = status;
    }

    public int getUid() {return uid;}
    public void setUid(int uid) {this.uid = uid;}
    @Nullable
    public Integer getAppWidgetId() {
        return appWidgetId;
    }
    public void setAppWidgetId(@Nullable Integer appWidgetId) {
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
    public boolean setNewLastWorkout(long lastWorkout) {
        if(this.lastWorkout == lastWorkout) {return false;}
        else {
            this.lastWorkout = lastWorkout;
            setNewStatus();
            return true;
        }
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
    public String getStatus() { return status;}
    public void setStatus(String status) {
        this.status = status;
    }
    public boolean setNewStatus() {
        String newStatus = getNewStatus(lastWorkout, intervalBlue);
        if(this.status.equals(newStatus)) {return false;}
        else {
            status = newStatus;
            return true;
        }
    }
    public boolean hasValidAppWidgetId() {
        boolean hasValidAppWidgetId = !(appWidgetId == null);
        Log.v(TAG, "hasValidAppWidgetId: " + hasValidAppWidgetId);
        return hasValidAppWidgetId;
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
        return "appWidgetId: " + appWidgetId + ", Title: " + title + ": ";
    }

    public void updateAfterClick(Context context) {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "start");

        int numberOfPastWorkouts = InteractWithPastWorkout.getCountOfActiveClickedWorkouts(context, uid) + 1;
        // Handler handler = new Handler(Looper.getMainLooper());
        // handler.post(() ->
        Toast.makeText(context, "Oh yeah! Already done this " + numberOfPastWorkouts + " times. :)", Toast.LENGTH_SHORT).show();

        // Update the widget data with the latest click
        status = STATUS_GREEN;
        lastWorkout = System.currentTimeMillis();
        // Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
        InteractWithPastWorkout.insertClickedWorkout(context, uid, lastWorkout);

        // Update the widget data in the db
        InteractWithWidgetInDb.updateWidget(context, this);

        // Instruct the widget manager to update the widget with the latest widget data
        runUpdate(context, false);

        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "complete\n------------------------------------------------------------------------");
    }

    // Can also be called on widget with invalid AppWidgetId
    public void updateWidgetBasedOnStatus(Context context) {
        Log.d(TAG, "updateBasedOnStatus: " + debugString() + "\n------------------------------------------------------------------------");

        // Update the widget data in the db (only) when there is a new status.
        if (setNewStatus()) {
            InteractWithWidgetInDb.updateWidget(context, this);
        }

        // Instruct the widget manager to update the widget with the latest widget data
        runUpdate(context, true);
    }

    public void runUpdate(Context context, boolean setOnClickListener) {
        // Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
        // Should only get this far if the appWidgetId is not null. But check nevertheless.
        if(hasValidAppWidgetId()) {AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, widgetView(context, setOnClickListener));}
        else {Log.d(TAG, "runUpdate: appWidgetId == null where it shouldn't be.");}
    }

    RemoteViews widgetView(Context context, boolean setOnClickListener) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.workout_pixel);
        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        if(setOnClickListener) {widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context));}
        // Before updating a widget, the text and background of the view need to be set. Otherwise, the existing not updated properties of the widgetView will be passed.
        widgetView.setTextViewText(R.id.appwidget_text, widgetText());
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", getDrawableIntFromStatus(status));
        return widgetView;
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
        if(appWidgetId == null) {
            Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.");
            return null;
        }
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}