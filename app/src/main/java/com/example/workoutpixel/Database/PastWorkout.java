package com.example.workoutpixel.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pastWorkouts")
public class PastWorkout {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "widgetUid")
    public int widgetUid;

    @ColumnInfo(name = "workoutTime")
    public long workoutTime;

    @ColumnInfo(name = "active")
    public boolean active = true;

    public PastWorkout(int widgetUid, long workoutTime) {
        // this.uid = uid;
        this.widgetUid = widgetUid;
        this.workoutTime = workoutTime;
    }


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getWidgetUid() {
        return widgetUid;
    }

    public void setWidgetUid(int widgetUid) {
        this.widgetUid = widgetUid;
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
