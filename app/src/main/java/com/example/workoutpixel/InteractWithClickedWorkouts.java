package com.example.workoutpixel;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractWithClickedWorkouts {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public static void addNewWorkoutToDataBase(Context context, int appWidgetId, long workoutTime){

        ClickedWorkout clickedWorkout = new ClickedWorkout(appWidgetId, workoutTime);

        workoutDao(context).insertClickedWorkout(clickedWorkout);

        // Close the DB to test the DB file
        // db.getOpenHelper().close();
    }

    public static List<ClickedWorkout> getClickedWorkoutsFromDbByAppWidgetId (Context context, int appWidgetId){
        Log.v(TAG, "getClickedWorkoutsFromDbByAppWidgetId " + workoutDao(context).loadAllByAppWidgetId(appWidgetId).size());
        return workoutDao(context).loadAllByAppWidgetId(appWidgetId);
    }

    public static void setActive(Context context, int uid, boolean active){
        Log.v(TAG, "setActive " + uid);

        // TODO: Uncomment as soon as function works
        // workoutDao(context).updateActiveByUid(uid, active);
    }

    public static void updateClickedWorkout(Context context, ClickedWorkout clickedWorkout){
        // TODO: Uncomment as soon as function works
        workoutDao(context).updateClickedWorkout(clickedWorkout);
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "clickedWorkout").allowMainThreadQueries().fallbackToDestructiveMigrationFrom(1).build();
        return db.workoutDao();
    }


/*
    public static ClickedWorkout getClickedWorkoutFromDb (Context context, int appWidgetId){
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "clickedWorkout").allowMainThreadQueries().build();
        WorkoutDao workoutDao = db.workoutDao();
        return workoutDao.loadAllByAppWidgetId(appWidgetId).get(0);
    }
*/

}
