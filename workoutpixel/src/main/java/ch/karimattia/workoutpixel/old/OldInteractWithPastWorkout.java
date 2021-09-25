package ch.karimattia.workoutpixel.old;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

@SuppressWarnings("unused")
public class OldInteractWithPastWorkout extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithClickedWorkouts";

    public OldInteractWithPastWorkout(@NonNull Application application) {
        super(application);
    }

    public static LiveData<List<OldPastWorkout>> getPastWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getPastWorkoutsFromDb");
        return goalDao(context).loadAllPastWorkouts(widgetUid);
    }

    public static int getCountOfActivePastWorkouts(Context context, int widgetUid) {
        Log.v(TAG, "getCountOfActivePastWorkouts ");
        return goalDao(context).getCountOfActivePastWorkouts(widgetUid);
    }

    public static void updatePastWorkout(Context context, OldPastWorkout clickedWorkout) {
        OldCommonFunctions.executorService.execute(() -> goalDao(context).updatePastWorkout(clickedWorkout));
    }

    public static void insertClickedWorkout(Context context, int widgetUid, long thisWorkoutTime) {
        OldPastWorkout clickedWorkout = new OldPastWorkout(widgetUid, thisWorkoutTime);
        OldCommonFunctions.executorService.execute(() -> goalDao(context).insertPastWorkout(clickedWorkout));
    }

    public static OldGoalDao goalDao(Context context) {
        OldAppDatabase db = OldAppDatabase.getDatabase(context);
        return db.goalDao();
    }
}
