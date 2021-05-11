package com.example.workoutpixel.Main;

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

import static com.example.workoutpixel.Core.CommonFunctions.*;

public class InteractWithWidgetInDb extends AndroidViewModel {
    private static final String TAG = "WORKOUT_PIXEL InteractWithWidgetInDb";

    public InteractWithWidgetInDb(@NonNull Application application) {
        super(application);
    }

    public static void updateWidget(Context context, Widget widget) {
        Log.d(TAG, "executorService updateWidget");
        executorService.execute(() -> workoutDao(context).updateWidget(widget));
        // executorService.shutdown();
        // executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    public static void saveDuringInitialize(Context context, Widget widget) {
        Log.d(TAG, "executorService saveDuringInitialize");
        executorService.execute(() -> workoutDao(context).insertWidget(widget));
        // executorService.shutdown();
        //executorService.awaitTermination(2, TimeUnit.MINUTES);
    }

    public static Widget loadWidgetByAppWidgetId(Context context, Integer appWidgetId) {
        Log.d(TAG, "getPastWorkoutsFromDbByAppWidgetId");
        return workoutDao(context).loadWidgetByAppWidgetId(appWidgetId);
    }

    public static Widget loadWidgetByUid(Context context, int uid) {
        Log.d(TAG, "getPastWorkoutsFromDbByUid");
        return workoutDao(context).loadWidgetByUid(uid);
    }


    public static LiveData<List<Widget>> loadAllWidgetsLiveData(Context context) {
        Log.d(TAG, "loadAllWidgetsLiveData");
        return workoutDao(context).loadAllWidgetsLiveData();
    }

    public static List<Widget> loadAllWidgets(Context context) {
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
        Log.d(TAG, "executorService setAppWidgetIdToNullByUid");
        executorService.execute(() -> workoutDao(context).setAppWidgetIdToNullByUid(uid));
        // executorService.shutdown();
        //executorService.awaitTermination(2, TimeUnit.MINUTES);
    }


    public static List<Widget> loadWidgetsWithoutValidAppWidgetId(Context context) {
        Log.d(TAG, "loadWidgetsWithoutValidAppWidgetId");
        return workoutDao(context).loadWidgetsWithoutValidAppWidgetId();
    }

    public static List<Widget> loadWidgetsWithValidAppWidgetId(Context context) {
        Log.d(TAG, "loadWidgetsWithValidAppWidgetId");
        return workoutDao(context).loadWidgetsWithValidAppWidgetId();
    }

    public static WorkoutDao workoutDao(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        return db.workoutDao();
    }
}
