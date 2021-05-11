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

public class InteractWithPastWorkout extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public InteractWithPastWorkout(@NonNull Application application) {
        super(application);
    }

    public static LiveData<List<PastWorkout>> getPastWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getPastWorkoutsFromDb");
        return workoutDao(context).loadAllPastWorkouts(widgetUid);
    }

    public static int getCountOfActiveClickedWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getCountOfActivePastWorkouts ");
        return workoutDao(context).getCountOfActivePastWorkouts(widgetUid);
    }

    public static void updatePastWorkout(Context context, PastWorkout clickedWorkout) {
        CommonFunctions.executorService.execute(() -> workoutDao(context).updatePastWorkout(clickedWorkout));
        // CommonFunctions.executorService.shutdown();
    }

    // Better to do by uid?
    public static void insertClickedWorkout(Context context, int widgetUid, long thisWorkoutTime) {
        PastWorkout clickedWorkout = new PastWorkout(widgetUid, thisWorkoutTime);
        CommonFunctions.executorService.execute(() -> workoutDao(context).insertPastWorkout(clickedWorkout));
        // CommonFunctions.executorService.shutdown();
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
