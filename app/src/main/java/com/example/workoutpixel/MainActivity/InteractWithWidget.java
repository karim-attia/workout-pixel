package com.example.workoutpixel.MainActivity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutpixel.Database.AppDatabase;
import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.Database.WorkoutDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InteractWithWidget extends AndroidViewModel {
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "WORKOUT_PIXEL PREFERENCES";

    public InteractWithWidget(@NonNull Application application) {
        super(application);
    }

    public static void updateWidget(Context context, Widget widget) {
        executorService.execute(() -> workoutDao(context).updateWidget(widget));
    }

    public static void saveDuringInitialize(Context context, Widget widget) {
        executorService.execute(() -> workoutDao(context).insertWidget(widget));
    }

    public static Widget loadWidgetByAppWidgetId(Context context, Integer appWidgetId) {
        Log.v(TAG, "getPastWorkoutsFromDbByAppWidgetId");
        return workoutDao(context).loadWidgetByAppWidgetId(appWidgetId);
    }

    public static Widget loadWidgetByUid(Context context, int uid) {
        Log.v(TAG, "getPastWorkoutsFromDbByUid");
        return workoutDao(context).loadWidgetByUid(uid);
    }


    public static LiveData<List<Widget>> loadAllWidgetsLiveData(Context context) {
        Log.v(TAG, "getWidgetsFromDb");
        return workoutDao(context).loadAllWidgetsLiveData();
    }

    public static List<Widget> loadAllWidgets(Context context) {
        Log.v(TAG, "getWidgetsFromDb");
        return workoutDao(context).loadAllWidgets();
    }

    public static void setAppWidgetIdToNull(Context context, Integer appWidgetId) {
        Log.v(TAG, "setAppWidgetIdToNull");
        executorService.execute(() -> workoutDao(context).setAppWidgetIdToNull(appWidgetId));
    }

    public static List<Widget> loadWidgetsWithoutValidAppWidgetId(Context context) {
        Log.v(TAG, "loadWidgetsWithInvalidAppWidgetId");
        return workoutDao(context).loadWidgetsWithoutValidAppWidgetId();
    }

    public static List<Widget> loadWidgetsWithValidAppWidgetId(Context context) {
        Log.v(TAG, "loadWidgetsWithInvalidAppWidgetId");
        return workoutDao(context).loadWidgetsWithValidAppWidgetId();
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
