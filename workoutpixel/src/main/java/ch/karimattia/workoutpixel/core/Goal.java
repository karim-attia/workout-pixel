package ch.karimattia.workoutpixel.core;

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

import ch.karimattia.workoutpixel.R;
import ch.karimattia.workoutpixel.database.GoalViewModel;
import ch.karimattia.workoutpixel.database.InteractWithPastWorkout;

@Entity(tableName = "goals")
public class Goal {
    private static final String TAG = "GOAL";

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
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    @ColumnInfo(name = "intervalRed")
    private int intervalRed;
    @ColumnInfo(name = "showDate")
    private boolean showDate;
    @ColumnInfo(name = "showTime")
    private boolean showTime;
    @ColumnInfo(name = "status")
    private String status;

    public Goal(@Nullable Integer appWidgetId, String title, long lastWorkout, int intervalBlue, int intervalRed, boolean showDate, boolean showTime, String status) {
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.lastWorkout = lastWorkout;
        this.intervalBlue = intervalBlue;
        this.intervalRed = intervalRed;
        this.showDate = showDate;
        this.showTime = showTime;
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

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
        if (this.lastWorkout == lastWorkout) {
            return false;
        } else {
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

// --Commented out by Inspection START (23.06.21, 20:29):
//    public void setIntervalRed(int intervalRed) {
//        this.intervalRed = intervalRed;
//    }
// --Commented out by Inspection STOP (23.06.21, 20:29)

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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean setNewStatus() {
        String newStatus = CommonFunctions.getNewStatus(lastWorkout, intervalBlue);
        if (this.status.equals(newStatus)) {
            return false;
        } else {
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
        return "widgetUid: " + uid + ", appWidgetId: " + appWidgetId + ", Title: " + title + ": ";
    }

    public void updateAfterClick(Context context) {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "start");

        int numberOfPastWorkouts = InteractWithPastWorkout.getCountOfActivePastWorkouts(context, uid) + 1;
        // Handler handler = new Handler(Looper.getMainLooper());
        // handler.post(() ->
        Toast.makeText(context, "Oh yeah! Already done this " + CommonFunctions.times(numberOfPastWorkouts) + " :)", Toast.LENGTH_SHORT).show();

        // Update the widget data with the latest click
        status = CommonFunctions.STATUS_GREEN;
        lastWorkout = System.currentTimeMillis();

        // Instruct the widget manager to update the widget with the latest widget data
        runUpdate(context, false);

        // Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
        InteractWithPastWorkout.insertClickedWorkout(context, uid, lastWorkout);

        // Update the widget data in the db
        GoalViewModel.updateGoal(context, this);

        Log.d(TAG, "ACTION_DONE_EXERCISE " + debugString() + "complete\n------------------------------------------------------------------------");
    }

    // Can also be called on widget with invalid AppWidgetId
    public void updateWidgetBasedOnStatus(Context context) {
        Log.v(TAG, "updateBasedOnStatus: " + debugString() + "\n------------------------------------------------------------------------");

        // Update the widget data in the db (only) when there is a new status.
        if (setNewStatus()) {
            GoalViewModel.updateGoal(context, this);
        }

        // Instruct the widget manager to update the widget with the latest widget data
        runUpdate(context, true);
    }

    // Can also be called on widget with invalid AppWidgetId
    public void runUpdate(Context context, boolean setOnClickListener) {
        // Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetId != null) {
            appWidgetManager.updateAppWidget(appWidgetId, widgetView(context, setOnClickListener, appWidgetManager));
        } else {
            Log.d(TAG, "runUpdate: appWidgetId == null " + debugString());
        }
    }

    RemoteViews widgetView(Context context, boolean setOnClickListener, AppWidgetManager appWidgetManager) {
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        if (setOnClickListener) {
            widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context));
        }
        // Before updating a widget, the text and background of the view need to be set. Otherwise, the existing not updated properties of the widgetView will be passed.
        widgetView.setTextViewText(R.id.appwidget_text, widgetText());
        widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", CommonFunctions.getDrawableIntFromStatus(status));
        // Set size if available
        // https://stackoverflow.com/questions/25153604/get-the-size-of-my-homescreen-widget
        if (appWidgetId != null) {
            int height = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 44);
            int padding_in_dp = (height - 44);
            int padding_in_px = (int) (padding_in_dp * context.getResources().getDisplayMetrics().density + 0.5f);
            int paddigTopInPx = (int) Math.ceil(padding_in_px / 4.0);
            int paddigBottomInPx = (int) Math.floor(padding_in_px / 4.0 * 3.0);
            widgetView.setViewPadding(R.id.widget_container, 0, paddigTopInPx, 0, paddigBottomInPx);
        }

        return widgetView;
    }

    // widgetText returns the text of the whole widget based on a Widget object.
    public String widgetText() {
        String widgetText = title;
        if (showDate & !status.equals(CommonFunctions.STATUS_NONE)) {
            widgetText += "\n" + CommonFunctions.dateBeautiful(lastWorkout);
        }
        if (showTime & !status.equals(CommonFunctions.STATUS_NONE)) {
            widgetText += "\n" + CommonFunctions.timeBeautiful(lastWorkout);
        }
        return widgetText;
    }

    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private PendingIntent widgetPendingIntent(Context context) {
        Intent intent = new Intent(context, WorkoutPixelAppWidgetProvider.class);
        intent.setAction(WorkoutPixelAppWidgetProvider.ACTION_DONE_EXERCISE);
        // put the appWidgetId as an extra to the update intent
        if (appWidgetId == null) {
            Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.");
            return null;
        }
        intent.putExtra("goalUid", uid);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}