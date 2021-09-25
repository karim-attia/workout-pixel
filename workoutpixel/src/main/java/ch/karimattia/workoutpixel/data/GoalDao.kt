package ch.karimattia.workoutpixel.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
	// Past Workouts
	// Return all workouts by appWidgetId
	@Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
	fun loadAllPastWorkouts(goalUid: Int): LiveData<List<PastWorkout>>

	@Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
	fun loadAllPastWorkoutsFlow(goalUid: Int): Flow<List<PastWorkout>>

	// Return number of active workouts by appWidgetId
	@Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:goalUid AND active='1'")
	fun getCountOfActivePastWorkouts(goalUid: Int): Int

	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout::class)
	fun insertPastWorkout(pastWorkout: PastWorkout)

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = PastWorkout::class)
	fun updatePastWorkout(pastWorkout: PastWorkout)

	// Widgets
	// Get goal by appWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
	fun loadGoalByAppWidgetId(appWidgetId: Int): Goal

	// Get goal by uid
	@Query("SELECT * FROM goals WHERE uid=:uid")
	fun loadGoalByUid(uid: Int): Goal

	@Query("SELECT * FROM goals WHERE uid=:uid")
	fun liveDataGoalByUid(uid: Int): LiveData<Goal>

	// Get all goals
	@Query("SELECT * FROM goals")
	fun loadAllGoalsLiveData(): LiveData<List<Goal>>

	// Get all goals
	@Query("SELECT * FROM goals")
	fun loadAllGoals(): List<Goal>

	// Get all goals
	@Query("SELECT * FROM goals")
	fun loadAllGoalsFlow(): Flow<List<Goal>>

	/*    // Get all goals with invalid appWidgetId
    @Query("SELECT * FROM goals WHERE appWidgetId IS NULL OR appWidgetId=0")
    Flow<List<Goal>> loadGoalsWithInvalidOrNullAppWidgetId();*/
	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
	fun insertGoal(goal: Goal): Long

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
	fun updateGoal(goal: Goal)

	@Query("UPDATE goals SET appWidgetId = null WHERE uid=:uid")
	fun setAppWidgetIdToNullByUid(uid: Int)

	@Query("UPDATE goals SET appWidgetId = null WHERE appWidgetId=:appWidgetId")
	fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int)

	// Get all goals without AppWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId IS NULL")
	fun loadGoalsWithoutValidAppWidgetId(): List<Goal>

	// Get all goals with AppWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId IS NOT NULL")
	fun loadGoalsWithValidAppWidgetId(): List<Goal>

	// Return number of goals
	@get:Query("SELECT COUNT() FROM goals")
	val countOfGoals: Int

	@Delete
	fun deleteGoal(goal: Goal)
}