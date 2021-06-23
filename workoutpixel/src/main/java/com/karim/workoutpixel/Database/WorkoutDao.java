package com.karim.workoutpixel.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkoutDao {
    // Past Workouts
    // Return all workouts by appWidgetId
    @Query("SELECT * FROM pastWorkouts WHERE widgetUid=:widgetUid ORDER BY workoutTime DESC")
    LiveData<List<PastWorkout>> loadAllPastWorkouts(int widgetUid);

    // Return number of active workouts by appWidgetId
    @Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:widgetUid AND active='1'")
    int getCountOfActivePastWorkouts(int widgetUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout.class)
    void insertPastWorkout(PastWorkout pastWorkout);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout.class)
    void updatePastWorkout(PastWorkout pastWorkout);

    // Widgets
    // Get widget by appWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
    Goal loadGoalByAppWidgetId(int appWidgetId);

    @Query("SELECT * FROM goals WHERE uid=:uid")
    Goal loadGoalByUid(int uid);

    @Query("SELECT * FROM goals WHERE uid=:uid")
    LiveData<Goal> liveDataGoalByUid(int uid);

    // Get all widgets
    @Query("SELECT * FROM goals")
    LiveData<List<Goal>> loadAllGoalsLiveData();

    // Get all widgets
    @Query("SELECT * FROM goals")
    List<Goal> loadAllGoals();

    // Get widget title by appWidgetId
    @Query("SELECT title FROM goals WHERE appWidgetId=:appWidgetId LIMIT 1")
    LiveData<String> loadWidgetTitle(int appWidgetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Goal.class)
    long insertGoal(Goal goal);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Goal.class)
    void updateGoal(Goal goal);

    @Query("UPDATE goals SET appWidgetId = null WHERE uid=:uid")
    void setAppWidgetIdToNullByUid(int uid);

    @Query("UPDATE goals SET appWidgetId = null WHERE appWidgetId=:appWidgetId")
    void setAppWidgetIdToNullByAppwidgetId(Integer appWidgetId);

    // Get all widgets without AppWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NULL")
    List<Goal> loadGoalsWithoutValidAppWidgetId();

    // Get all widgets without AppWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NOT NULL")
    List<Goal> loadGoalsWithValidAppWidgetId();

    // Return number of goals
    @Query("SELECT COUNT() FROM goals")
    int getCountOfGoals();

    @Delete
    void deleteGoal(Goal goal);
}
