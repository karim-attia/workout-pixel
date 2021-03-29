package com.example.workoutpixel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM clickedWorkout")
    List<ClickedWorkout> getAll();

    // Return all workouts by appWidgetId
    @Query("SELECT * FROM clickedWorkout WHERE appWidgetId=:appWidgetId")
    List<ClickedWorkout> loadAllByAppWidgetId(int appWidgetId);

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
}
