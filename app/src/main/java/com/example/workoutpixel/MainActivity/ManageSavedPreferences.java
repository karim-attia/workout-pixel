package com.example.workoutpixel.MainActivity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.workoutpixel.Core.Widget;
import com.example.workoutpixel.Database.AppDatabase;
import com.example.workoutpixel.Database.WorkoutDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ManageSavedPreferences manages the preferences for every widget. This is either done through the appWidgetId of the widget or through a Widget object.
// Could move them all to the database into a new table at some point
public class ManageSavedPreferences extends AndroidViewModel {
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "WORKOUT_PIXEL PREFERENCES";

    public ManageSavedPreferences(@NonNull Application application) {
        super(application);
    }

    public static void updateWidget(Context context, Widget widget) {
        executorService.execute(() -> workoutDao(context).updateWidget(widget));
    }

    public static void saveDuringInitialize(Context context, Widget widget) {
        executorService.execute(() -> workoutDao(context).insertWidget(widget));
    }

    public static Widget loadWidgetByAppWidgetId(Context context, int appWidgetId) {
        Log.v(TAG, "getPastWorkoutsFromDbByAppWidgetId");
        return workoutDao(context).loadWidgetById(appWidgetId);
    }

    public static LiveData<List<Widget>> loadAllWidgetsLiveData(Context context) {
        Log.v(TAG, "getWidgetsFromDb");
        return workoutDao(context).loadAllWidgetsLiveData();
    }

    public static List<Widget> loadAllWidgets(Context context) {
        Log.v(TAG, "getWidgetsFromDb");
        return workoutDao(context).loadAllWidgets();
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
