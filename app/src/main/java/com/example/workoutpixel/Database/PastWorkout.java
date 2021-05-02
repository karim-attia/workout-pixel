package com.example.workoutpixel.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pastWorkouts")
public class PastWorkout {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "appWidgetId")
    public int appWidgetId;

    @ColumnInfo(name = "workoutTime")
    public long workoutTime;

    @ColumnInfo(name = "active")
    public boolean active = true;

    public PastWorkout(int appWidgetId, long workoutTime) {
        // this.uid = uid;
        this.appWidgetId = appWidgetId;
        this.workoutTime = workoutTime;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public long getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(long workoutTime) {
        this.workoutTime = workoutTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
