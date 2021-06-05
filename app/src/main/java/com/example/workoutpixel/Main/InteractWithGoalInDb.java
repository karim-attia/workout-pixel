package com.example.workoutpixel.Main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutpixel.Database.AppDatabase;
import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Database.WorkoutDao;

import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.*;

public class InteractWithGoalInDb extends AndroidViewModel {
    private static final String TAG = "InteractWithWidgetInDb";

    public InteractWithGoalInDb(@NonNull Application application) {
        super(application);
    }

    public static void updateGoal(Context context, Goal goal) {
        Log.d(TAG, "executorService updateWidget");
        executorService.execute(() -> workoutDao(context).updateWidget(goal));
        // executorService.shutdown();
        // executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    public static int saveDuringInitialize(Context context, Goal goal) {
        Log.d(TAG, "executorService saveDuringInitialize");
        return ((int) workoutDao(context).insertWidget(goal));
    }

    public static Goal loadGoalByAppWidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "getPastWorkoutsFromDbByAppWidgetId" + appWidgetId);
        return workoutDao(context).loadWidgetByAppWidgetId(appWidgetId);
    }

    public static Goal loadGoalByUid(Context context, int uid) {
        Log.d(TAG, "getPastWorkoutsFromDbByUid");
        // TODO: widget -> goal
        return workoutDao(context).loadWidgetByUid(uid);
    }


    public static LiveData<List<Goal>> loadAllGoalsLiveData(Context context) {
        Log.d(TAG, "loadAllWidgetsLiveData");
        return workoutDao(context).loadAllWidgetsLiveData();
    }

    public static List<Goal> loadAllGoals(Context context) {
        Log.d(TAG, "loadAllWidgets");
        return workoutDao(context).loadAllWidgets();
    }

    public static void setAppWidgetIdToNullByAppwidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByAppwidgetId");
        executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByAppwidgetId(appWidgetId));
        // executorService.shutdown();
        //executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    public static void setAppWidgetIdToNullByUid(Context context, int uid) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByUid: " + uid);
        executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByUid(uid));
    }

    public static List<Goal> loadWidgetsWithoutValidAppWidgetId(Context context) {
        Log.d(TAG, "loadWidgetsWithoutValidAppWidgetId");
        return workoutDao(context).loadWidgetsWithoutValidAppWidgetId();
    }

    public static List<Goal> loadWidgetsWithValidAppWidgetId(Context context) {
        Log.d(TAG, "loadWidgetsWithValidAppWidgetId");
        return workoutDao(context).loadWidgetsWithValidAppWidgetId();
    }

    public static void deleteGoal(Context context, Goal goal) {
        Log.d(TAG, "executorService deleteGoal: " + goal.debugString());
        executorService.execute(() -> workoutDao(context).deleteGoal(goal));
    }

    public static int getCountOfGoals(Context context) {
        Log.d(TAG, "getCountOfGoals");
        return workoutDao(context).getCountOfGoals();
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
