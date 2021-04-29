package com.example.workoutpixel.ClickedWorkouts;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutpixel.Database.AppDatabase;
import com.example.workoutpixel.Database.WorkoutDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClickedWorkoutViewModel extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ClickedWorkoutViewModel(@NonNull Application application) {
        super(application);
    }

    public static LiveData<List<ClickedWorkout>> getClickedWorkoutsFromDbByAppWidgetId (Context context, int appWidgetId){
        Log.v(TAG, "getClickedWorkoutsFromDbByAppWidgetId ");
        return workoutDao(context).loadAllByAppWidgetId(appWidgetId);
    }

    public static LiveData<List<ClickedWorkout>> getAllActiveClickedWorkoutsFromDbByAppWidgetId (Context context, int appWidgetId){
        Log.v(TAG, "getClickedWorkoutsFromDbByAppWidgetId ");
        return workoutDao(context).loadAllActiveByAppWidgetId(appWidgetId);
    }

    public static void updateClickedWorkout(Context context, ClickedWorkout clickedWorkout){
        executorService.execute(() -> workoutDao(context).updateClickedWorkout(clickedWorkout));
    }

    public static void insertClickedWorkout(Context context, int appWidgetId, long thisWorkoutTime){
        ClickedWorkout clickedWorkout = new ClickedWorkout(appWidgetId, thisWorkoutTime);
        executorService.execute(() -> workoutDao(context).insertClickedWorkout(clickedWorkout));
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
