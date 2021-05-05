package com.example.workoutpixel.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "widgets")
public class Widget {
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
        return this.title;
    }
}