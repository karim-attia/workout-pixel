package ch.karimattia.workoutpixel.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.karimattia.workoutpixel.core.Goal;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface GoalDao {
    // Past Workouts
    // Return all workouts by appWidgetId
    @Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
    LiveData<List<PastWorkout>> loadAllPastWorkouts(int goalUid);

    @Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
    Flow<List<PastWorkout>> loadAllPastWorkoutsFlow(int goalUid);

    // Return number of active workouts by appWidgetId
    @Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:goalUid AND active='1'")
    int getCountOfActivePastWorkouts(int goalUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout.class)
    void insertPastWorkout(PastWorkout pastWorkout);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout.class)
    void updatePastWorkout(PastWorkout pastWorkout);

    // Widgets

    // Get goal by appWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
    Goal loadGoalByAppWidgetId(int appWidgetId);

    // Get goal by uid
    @Query("SELECT * FROM goals WHERE uid=:uid")
    Goal loadGoalByUid(int uid);

    @Query("SELECT * FROM goals WHERE uid=:uid")
    LiveData<Goal> liveDataGoalByUid(int uid);

    // Get all goals
    @Query("SELECT * FROM goals")
    LiveData<List<Goal>> loadAllGoalsLiveData();

    // Get all goals
    @Query("SELECT * FROM goals")
    List<Goal> loadAllGoals();

    // Get all goals
    @Query("SELECT * FROM goals")
    Flow<List<Goal>> loadAllGoalsFlow();

/*    // Get all goals with invalid appWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NULL OR appWidgetId=0")
    Flow<List<Goal>> loadGoalsWithInvalidOrNullAppWidgetId();*/

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
