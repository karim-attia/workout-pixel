package com.example.workoutpixel;

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
    @Query("SELECT * FROM clickedWorkout")
    List<ClickedWorkout> getAll();

    // Return all workouts by appWidgetId
    @Query("SELECT * FROM clickedWorkout WHERE appWidgetId=:appWidgetId ORDER BY workoutTime DESC")
    LiveData<List<ClickedWorkout>> loadAllByAppWidgetId(int appWidgetId);

    // Return all workouts by appWidgetId that are active
    @Query("SELECT * FROM clickedWorkout WHERE appWidgetId=:appWidgetId AND active='1' ORDER BY workoutTime DESC")
    LiveData<List<ClickedWorkout>> loadAllActiveByAppWidgetId(int appWidgetId);

/*
    // Return only workoutTime from all workouts by appWidgetId
    @Query("SELECT workoutTime FROM clickedWorkout WHERE appWidgetId=:appWidgetId")
    List<ClickedWorkout> loadWorkoutTimeByAppWidgetId(int appWidgetId);
*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClickedWorkout(ClickedWorkout clickedWorkout);

    @Insert
    void insertAll(ClickedWorkout... clickedWorkout);

    @Delete
    void delete(ClickedWorkout clickedWorkout);

    // Update active by Uid
    // TODO: Query
    // @Query("SELECT * FROM clickedWorkout WHERE uid=:uid")
    // void updateActiveByUid(int uid, boolean active);

    @Update
    public void updateClickedWorkout(ClickedWorkout clickedWorkout);

    @Query("SELECT COUNT(appWidgetId) FROM clickedWorkout WHERE appWidgetId=:appWidgetId")
    LiveData<Integer> getCount(int appWidgetId);

    @Query("SELECT COUNT(appWidgetId) FROM clickedWorkout WHERE appWidgetId=:appWidgetId AND active='1'")
    LiveData<Integer> getCountActive(int appWidgetId);
}
