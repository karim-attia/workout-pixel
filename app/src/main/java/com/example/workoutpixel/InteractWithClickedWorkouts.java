package com.example.workoutpixel;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractWithClickedWorkouts {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public static void addNewWorkoutToDataBase(Context context, int appWidgetId, long workoutTime){

        final int NUMBER_OF_THREADS = 4;
        final ExecutorService databaseWriteExecutor =
                Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        Log.v(TAG, "1");
        ClickedWorkout clickedWorkout = new ClickedWorkout(appWidgetId, workoutTime);
        Log.v(TAG, "2");

        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "clickedWorkout").allowMainThreadQueries().build();

        Log.v(TAG, "3");
        WorkoutDao workoutDao = db.workoutDao();
        Log.v(TAG, "4");

/*

        AppDatabase.databaseWriteExecutor.execute(() -> {
            workoutDao.insertClickedWorkout(clickedWorkout);
        });

*/

        workoutDao.insertClickedWorkout(clickedWorkout);

        long testTime = workoutDao.loadAllByAppWidgetId(appWidgetId).get(0).getWorkoutTime();
        int testId = workoutDao.loadAllByAppWidgetId(appWidgetId).get(0).getAppWidgetId();

        Log.v(TAG, "5 " + testTime + " " + testId + "");

        // Close the DB to test the DB file
        // db.getOpenHelper().close();
    }

}
