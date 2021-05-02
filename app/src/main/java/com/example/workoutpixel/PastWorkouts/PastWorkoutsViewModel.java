package com.example.workoutpixel.PastWorkouts;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutpixel.Core.CommonFunctions;
import com.example.workoutpixel.Database.AppDatabase;
import com.example.workoutpixel.Database.PastWorkout;
import com.example.workoutpixel.Database.WorkoutDao;

import java.util.List;

public class PastWorkoutsViewModel extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public PastWorkoutsViewModel(@NonNull Application application) {
        super(application);
    }

    public static LiveData<List<PastWorkout>> getPastWorkoutsFromDbByAppWidgetId(Context context, int appWidgetId) {
        Log.v(TAG, "getClickedWorkoutsFromDbByAppWidgetId ");
        return workoutDao(context).loadAllPastWorkouts(appWidgetId);
    }

    public static int getCountOfActiveClickedWorkouts(Context context, int appWidgetId) {
        Log.v(TAG, "getCountOfActiveClickedWorkouts ");
        return workoutDao(context).getCountOfActivePastWorkouts(appWidgetId);
    }

    public static void updatePastWorkout(Context context, PastWorkout clickedWorkout) {
        CommonFunctions.executorService.execute(() -> workoutDao(context).updatePastWorkout(clickedWorkout));
    }

    public static void insertClickedWorkout(Context context, int appWidgetId, long thisWorkoutTime) {
        PastWorkout clickedWorkout = new PastWorkout(appWidgetId, thisWorkoutTime);
        CommonFunctions.executorService.execute(() -> workoutDao(context).insertPastWorkout(clickedWorkout));
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
