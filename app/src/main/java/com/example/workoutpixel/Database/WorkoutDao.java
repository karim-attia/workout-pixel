package com.example.workoutpixel.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkoutDao {
    // Past Workouts
    // Return all workouts by appWidgetId
    @Query("SELECT * FROM pastWorkouts WHERE appWidgetId=:appWidgetId ORDER BY workoutTime DESC")
    LiveData<List<PastWorkout>> loadAllPastWorkouts(int appWidgetId);

    // Return all workouts by appWidgetId that are active
    @Query("SELECT COUNT() FROM pastWorkouts WHERE appWidgetId=:appWidgetId AND active='1'")
    int getCountOfActivePastWorkouts(int appWidgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPastWorkout(PastWorkout pastWorkout);

    @Update
    void updatePastWorkout(PastWorkout pastWorkout);

    // Widgets
    // Get widget by appWidgetId
    @Query("SELECT * FROM widgets WHERE appWidgetId=:appWidgetId")
    Widget loadWidgetById(int appWidgetId);

    // Get all widgets
    @Query("SELECT * FROM widgets")
    LiveData<List<Widget>> loadAllWidgetsLiveData();

    // Get all widgets
    @Query("SELECT * FROM widgets")
    List<Widget> loadAllWidgets();

    // Get widget title by appWidgetId
    @Query("SELECT title FROM widgets WHERE appWidgetId=:appWidgetId LIMIT 1")
    LiveData<String> loadWidgetTitle(int appWidgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWidget(Widget widget);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Widget.class)
    void updateWidget(Widget widget);
}
