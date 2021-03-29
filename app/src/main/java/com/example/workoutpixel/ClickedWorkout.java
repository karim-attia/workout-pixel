package com.example.workoutpixel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clickedWorkout")
public class ClickedWorkout {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "appWidgetId")
    public int appWidgetId;

    @ColumnInfo(name = "workoutTime")
    public long workoutTime;

    public ClickedWorkout(int appWidgetId, long workoutTime){
        // this.uid = uid;
        this.appWidgetId = appWidgetId;
        this.workoutTime = workoutTime;
    }

    public int getUid() {
        return uid;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public long getWorkoutTime() {
        return workoutTime;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public void setWorkoutTime(long workoutTime) {
        this.workoutTime = workoutTime;
    }
}
