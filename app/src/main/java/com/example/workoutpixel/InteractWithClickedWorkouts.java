package com.example.workoutpixel;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractWithClickedWorkouts {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public static void addNewWorkoutToDataBase(Context context, int appWidgetId, long workoutTime){
        ClickedWorkout clickedWorkout = new ClickedWorkout(appWidgetId, workoutTime);
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "clickedWorkout").allowMainThreadQueries().fallbackToDestructiveMigrationFrom(1).build();

        WorkoutDao workoutDao = db.workoutDao();

        workoutDao.insertClickedWorkout(clickedWorkout);

        long testTime = workoutDao.loadAllByAppWidgetId(appWidgetId).get(0).getWorkoutTime();
        int testId = workoutDao.loadAllByAppWidgetId(appWidgetId).get(0).getAppWidgetId();

        // Close the DB to test the DB file
        // db.getOpenHelper().close();
    }

/*
    public static ClickedWorkout getClickedWorkoutFromDb (Context context, int appWidgetId){
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "clickedWorkout").allowMainThreadQueries().build();
        WorkoutDao workoutDao = db.workoutDao();
        return workoutDao.loadAllByAppWidgetId(appWidgetId).get(0);
    }
*/

}
