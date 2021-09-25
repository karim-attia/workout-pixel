package ch.karimattia.workoutpixel.old;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class OldGoalViewModel extends AndroidViewModel {
    private static final String TAG = "GoalViewModel";

    private final LiveData<List<OldGoal>> allGoals;
    OldGoalRepository repository;

    public OldGoalViewModel(Application application) {
        super(application);
        repository = new OldGoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public LiveData<List<OldGoal>> getAllGoals() { return allGoals; }

    // public void insert(Goal goal) { repository.insert(goal); }


    public static void updateGoal(Context context, OldGoal goal) {
        Log.d(TAG, "executorService updateWidget " + goal.debugString());
        Log.d(TAG, "executorService updateWidget getIntervalBlue " + goal.getIntervalBlue());
        OldCommonFunctions.executorService.execute(() -> workoutDao(context).updateGoal(goal));
    }

    public static int saveDuringInitialize(Context context, OldGoal goal) {
        Log.d(TAG, "executorService saveDuringInitialize " + goal.debugString());
        return ((int) workoutDao(context).insertGoal(goal));
    }

    public static Goal loadGoalByAppWidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "getPastWorkoutsFromDbByAppWidgetId" + appWidgetId);
        return workoutDao(context).loadGoalByAppWidgetId(appWidgetId);
    }

    public static Goal loadGoalByUid(Context context, int uid) {
        Log.d(TAG, "loadGoalByUid " + uid);
        return workoutDao(context).loadGoalByUid(uid);
    }

    public static LiveData<OldGoal> liveDataGoalByUid(Context context, int uid) {
        Log.d(TAG, "getPastWorkoutsFromDbByUid " + uid);
        return workoutDao(context).liveDataGoalByUid(uid);
    }

    public static LiveData<List<OldGoal>> loadAllGoalsLiveData(Context context) {
        Log.d(TAG, "loadAllWidgetsLiveData");
        return workoutDao(context).loadAllGoalsLiveData();
    }

    public static List<OldGoal> loadAllGoals(Context context) {
        Log.d(TAG, "loadAllGoals");
        return workoutDao(context).loadAllGoals();
    }

    public static void setAppWidgetIdToNullByAppwidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByAppwidgetId" + appWidgetId);
        OldCommonFunctions.executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByAppwidgetId(appWidgetId));
    }

    public static void setAppWidgetIdToNullByUid(Context context, int uid) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByUid: " + uid);
        OldCommonFunctions.executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByUid(uid));
    }

    public static List<OldGoal> loadGoalsWithoutValidAppWidgetId(Context context) {
        Log.d(TAG, "loadGoalsWithoutValidAppWidgetId");
        return workoutDao(context).loadGoalsWithoutValidAppWidgetId();
    }

    public static List<OldGoal> loadGoalsWithValidAppWidgetId(Context context) {
        Log.d(TAG, "loadGoalsWithValidAppWidgetId");
        return workoutDao(context).loadGoalsWithValidAppWidgetId();
    }

    public static void deleteGoal(Context context, OldGoal goal) {
        Log.d(TAG, "executorService deleteGoal: " + goal.debugString());
        OldCommonFunctions.executorService.execute(() -> workoutDao(context).deleteGoal(goal));
    }

    public static int getCountOfGoals(Context context) {
        Log.d(TAG, "getCountOfGoals");
        return workoutDao(context).getCountOfGoals();
    }

    public static OldGoalDao workoutDao(Context context) {
        OldAppDatabase db = OldAppDatabase.getDatabase(context);
        return db.goalDao();
    }
}
