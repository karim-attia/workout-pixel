package ch.karimattia.workoutpixel.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.karimattia.workoutpixel.database.PastWorkout
import ch.karimattia.workoutpixel.core.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDaoKotlin {
    // Past Workouts
    // Return all workouts by appWidgetId
    @Query("SELECT * FROM pastWorkouts WHERE widgetUid=:widgetUid ORDER BY workoutTime DESC")
    fun loadAllPastWorkouts(widgetUid: Int): LiveData<List<PastWorkout?>?>?

    // Return number of active workouts by appWidgetId
    @Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:widgetUid AND active='1'")
    fun getCountOfActivePastWorkouts(widgetUid: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout::class)
    fun insertPastWorkout(pastWorkout: PastWorkout?)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout::class)
    fun updatePastWorkout(pastWorkout: PastWorkout?)

    // Widgets
    // --Commented out by Inspection START (24.06.21, 12:21):
    //    // Get widget by appWidgetId
    //    @Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
    //    Goal loadGoalByAppWidgetId(int appWidgetId);
    // --Commented out by Inspection STOP (24.06.21, 12:21)
    @Query("SELECT * FROM goals WHERE uid=:uid")
    fun loadGoalByUid(uid: Int): Goal

    @Query("SELECT * FROM goals WHERE uid=:uid")
    fun liveDataGoalByUid(uid: Int): LiveData<Goal>

    // Get all widgets
    @Query("SELECT * FROM goals")
    fun loadAllGoalsFlow(): Flow<List<Goal>>

    // Get all widgets
    @Query("SELECT * FROM goals")
    fun loadAllGoalsLiveData(): LiveData<List<Goal>>

    // Get all widgets
    @Query("SELECT * FROM goals")
    suspend fun loadAllGoalsCoroutine(): List<Goal>

    // Get all widgets
    @Query("SELECT * FROM goals")
    fun loadAllGoals(): List<Goal>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
    fun insertGoal(goal: Goal): Long

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
    fun updateGoal(goal: Goal)

    @Query("UPDATE goals SET appWidgetId = null WHERE uid=:uid")
    fun setAppWidgetIdToNullByUid(uid: Int)

    @Query("UPDATE goals SET appWidgetId = null WHERE appWidgetId=:appWidgetId")
    fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int?)

    // Get all widgets without AppWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NULL")
    fun loadGoalsWithoutValidAppWidgetId(): List<Goal>

    // Get all widgets without AppWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NOT NULL")
    fun loadGoalsWithValidAppWidgetId(): List<Goal>

    // Return number of goals
    @get:Query("SELECT COUNT() FROM goals")
    val countOfGoals: Int

    @Delete
    fun deleteGoal(goal: Goal)
}