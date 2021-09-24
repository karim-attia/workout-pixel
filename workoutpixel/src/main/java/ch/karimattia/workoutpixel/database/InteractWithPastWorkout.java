package ch.karimattia.workoutpixel.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ch.karimattia.workoutpixel.core.CommonFunctions;

@SuppressWarnings("unused")
public class InteractWithPastWorkout extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public InteractWithPastWorkout(@NonNull Application application) {
        super(application);
    }

    public static LiveData<List<PastWorkout>> getPastWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getPastWorkoutsFromDb");
        return goalDao(context).loadAllPastWorkouts(widgetUid);
    }

    public static int getCountOfActivePastWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getCountOfActivePastWorkouts ");
        return goalDao(context).getCountOfActivePastWorkouts(widgetUid);
    }

    public static void updatePastWorkout(Context context, PastWorkout clickedWorkout) {
        CommonFunctions.executorService.execute(() -> goalDao(context).updatePastWorkout(clickedWorkout));
    }

    public static void insertClickedWorkout(Context context, int widgetUid, long thisWorkoutTime) {
        PastWorkout clickedWorkout = new PastWorkout(widgetUid, thisWorkoutTime);
        CommonFunctions.executorService.execute(() -> goalDao(context).insertPastWorkout(clickedWorkout));
    }

    public static GoalDao goalDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.goalDao();
    }
}
