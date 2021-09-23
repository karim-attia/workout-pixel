package ch.karimattia.workoutpixel.core;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
        // this.context = context;
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

// --Commented out by Inspection START (23.06.21, 20:29):
//    public void setIntervalRed(int intervalRed) {
//        this.intervalRed = intervalRed;
//    }
// --Commented out by Inspection STOP (23.06.21, 20:29)

    public int getIntervalRed() {
        return intervalRed;
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

    // Single responsibility principle: New class that takes goal: Goal and context: Context as input?

    public String debugString() {
        return "widgetUid: " + uid + ", appWidgetId: " + appWidgetId + ", Title: " + title + ": ";
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

    public Goal copy() {
        Goal goalCopy = new Goal(
                appWidgetId,
                title,
                lastWorkout,
                intervalBlue,
                intervalRed,
                showDate,
                showTime,
                status
        );
        goalCopy.setUid(uid);
        return goalCopy;
    }
}