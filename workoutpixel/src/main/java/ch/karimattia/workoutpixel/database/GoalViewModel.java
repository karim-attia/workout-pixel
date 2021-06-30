package ch.karimattia.workoutpixel.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ch.karimattia.workoutpixel.core.CommonFunctions;
import ch.karimattia.workoutpixel.core.Goal;

public class GoalViewModel extends AndroidViewModel {
    private static final String TAG = "InteractWithWidgetInDb";

    private final LiveData<List<Goal>> allGoals;

    public GoalViewModel(Application application) {
        super(application);
        GoalRepository repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public LiveData<List<Goal>> getAllGoals() { return allGoals; }

    // public void insert(Goal goal) { repository.insert(goal); }


    public static void updateGoal(Context context, Goal goal) {
        Log.d(TAG, "executorService updateWidget " + goal.debugString());
        CommonFunctions.executorService.execute(() -> workoutDao(context).updateGoal(goal));
    }

    public static int saveDuringInitialize(Context context, Goal goal) {
        Log.d(TAG, "executorService saveDuringInitialize " + goal.debugString());
        return ((int) workoutDao(context).insertGoal(goal));
    }

// --Commented out by Inspection START (23.06.21, 20:47):
//    public static Goal loadGoalByAppWidgetId(Context context, Integer appWidgetId) {
//        Log.d(TAG, "getPastWorkoutsFromDbByAppWidgetId" + appWidgetId);
//        return workoutDao(context).loadGoalByAppWidgetId(appWidgetId);
//    }
// --Commented out by Inspection STOP (23.06.21, 20:47)

    public static Goal loadGoalByUid(Context context, int uid) {
        Log.d(TAG, "loadGoalByUid " + uid);
        return workoutDao(context).loadGoalByUid(uid);
    }

    public static LiveData<Goal> liveDataGoalByUid(Context context, int uid) {
        Log.d(TAG, "getPastWorkoutsFromDbByUid " + uid);
        return workoutDao(context).liveDataGoalByUid(uid);
    }

    public static LiveData<List<Goal>> loadAllGoalsLiveData(Context context) {
        Log.d(TAG, "loadAllWidgetsLiveData");
        return workoutDao(context).loadAllGoalsLiveData();
    }

    public static List<Goal> loadAllGoals(Context context) {
        Log.d(TAG, "loadAllGoals");
        return workoutDao(context).loadAllGoals();
    }

    public static void setAppWidgetIdToNullByAppwidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByAppwidgetId" + appWidgetId);
        CommonFunctions.executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByAppwidgetId(appWidgetId));
    }

    public static void setAppWidgetIdToNullByUid(Context context, int uid) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByUid: " + uid);
        CommonFunctions.executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByUid(uid));
    }

    public static List<Goal> loadGoalsWithoutValidAppWidgetId(Context context) {
        Log.d(TAG, "loadGoalsWithoutValidAppWidgetId");
        return workoutDao(context).loadGoalsWithoutValidAppWidgetId();
    }

    public static List<Goal> loadGoalsWithValidAppWidgetId(Context context) {
        Log.d(TAG, "loadGoalsWithValidAppWidgetId");
        return workoutDao(context).loadGoalsWithValidAppWidgetId();
    }

    public static void deleteGoal(Context context, Goal goal) {
        Log.d(TAG, "executorService deleteGoal: " + goal.debugString());
        CommonFunctions.executorService.execute(() -> workoutDao(context).deleteGoal(goal));
    }

    public static int getCountOfGoals(Context context) {
        Log.d(TAG, "getCountOfGoals");
        return workoutDao(context).getCountOfGoals();
    }

    public static GoalDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.goalDao();
    }
}
