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

// ManageSavedPreferences manages the preferences for every widget. This is either done through the appWidgetId of the widget or through a Widget object.
// Could move them all to the database into a new table at some point
public class ManageSavedPreferences extends AndroidViewModel {
    public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String PREFS_NAME = "com.example.WorkoutPixel";
    private static final String PREF_PREFIX_KEY_TITLE = "appwidget_title_";
    private static final String PREF_PREFIX_KEY_INTERVAL_BLUE = "appwidget_interval_blue";
    private static final String PREF_PREFIX_KEY_INTERVAL_RED = "appwidget_interval_red";
    private static final String PREF_PREFIX_KEY_LAST_WORKOUT = "appwidget_last_workout";
    private static final String PREF_PREFIX_KEY_CURRENT_STATUS = "appwidget_current_status";
    private static final String PREF_PREFIX_KEY_SHOW_DATE = "appwidget_show_date";
    private static final String PREF_PREFIX_KEY_SHOW_TIME = "appwidget_show_time";
    private static final String TAG = "WORKOUT_PIXEL PREFERENCES";

    public ManageSavedPreferences(@NonNull Application application) {
        super(application);
    }

    public static void updateWidget(Context context, Widget widget) {
        executorService.execute(() -> workoutDao(context).updateWidget(widget));
    }

    public static void saveDuringInitialize(Context context, Widget widget) {
/*
        int appWidgetId = widget.getAppWidgetId();
        saveTitle(context, appWidgetId, widget.getTitle());
        saveIntervalBlue(context, appWidgetId, widget.getIntervalBlue());
        saveIntervalRed(context, appWidgetId, widget.getIntervalRed());
        saveShowDate(context, appWidgetId, widget.getShowDate());
        saveShowTime(context, appWidgetId, widget.getShowTime());
*/
        executorService.execute(() -> workoutDao(context).insertWidget(widget));
    }
/*
    public static void deleteAll(Context context, int appWidgetId) {
        deleteTitle(context, appWidgetId);
        deleteIntervalBlue(context, appWidgetId);
        deleteIntervalRed(context, appWidgetId);
        deleteLastWorkout(context, appWidgetId);
        deleteCurrentStatus(context, appWidgetId);
        deleteShowDate(context, appWidgetId) ;
        deleteShowTime(context, appWidgetId);
    }*/

/*
    public static Widget loadWidget(Context context, int appWidgetId) {
        return new Widget(appWidgetId, loadTitle(context, appWidgetId), loadLastWorkout(context, appWidgetId), loadIntervalBlue(context, appWidgetId), loadIntervalRed(context, appWidgetId), loadShowDate(context, appWidgetId), loadShowTime(context, appWidgetId), loadCurrentStatus(context, appWidgetId));
    }
*/

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
