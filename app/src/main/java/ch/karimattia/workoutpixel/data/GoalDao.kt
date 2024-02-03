package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import androidx.room.*
import ch.karimattia.workoutpixel.core.daysToMilliseconds
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
	// Goals
	// Get goal by appWidgetId
	@Query("SELECT * FROM Goal WHERE appWidgetId=:appWidgetId")
	suspend fun loadGoalByAppWidgetId(appWidgetId: Int): Goal?

	// Get goal by uid
	@Query("SELECT * FROM Goal WHERE uid=:uid")
	suspend fun loadGoalByUid(uid: Int): Goal?

	// Get goal by uid
	@Query("SELECT * FROM Goal WHERE appWidgetId=:appWidgetId")
	fun loadGoalByAppWidgetIdFlow(appWidgetId: Int): Flow<Goal>

	// Get all goals
	@Query("SELECT * FROM Goal")
	fun loadAllGoalsFlow(): Flow<List<Goal>>

	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = GoalWithoutCount::class)
	suspend fun insertGoal(goal: GoalWithoutCount): Long

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = GoalWithoutCount::class)
	suspend fun updateGoal(goal: GoalWithoutCount)

	@Query("UPDATE goals SET appWidgetId =:invalidAppWidgetId WHERE uid=:uid")
	suspend fun setAppWidgetIdToNullByUid(uid: Int, invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID)

	@Query("UPDATE goals SET appWidgetId =:invalidAppWidgetId WHERE appWidgetId=:appWidgetId")
	suspend fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int, invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID)

	// Get all goals
	@Query("SELECT * FROM Goal")
	suspend fun loadAllGoals(): List<Goal>

	// Get all goals without AppWidgetId
	@Query("SELECT * FROM Goal WHERE appWidgetId =:invalidAppWidgetId")
	fun loadGoalsWithoutValidAppWidgetId(invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): Flow<List<Goal>>

	// Get all goals with AppWidgetId
	@Query("SELECT * FROM Goal WHERE appWidgetId !=:invalidAppWidgetId")
	suspend fun loadGoalsWithValidAppWidgetId(invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): List<Goal>

	@Delete
	suspend fun deleteGoal(goal: GoalWithoutCount)


	// Past Workouts
	@Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
	fun loadPastWorkoutsByGoalUid(goalUid: Int): Flow<List<PastClick>>

	@Query("SELECT * FROM pastWorkouts WHERE workoutTime > :lastWeek AND active ORDER BY workoutTime DESC")
	fun activePastClicksLastWeek(lastWeek: Long = System.currentTimeMillis() - daysToMilliseconds(7)): Flow<List<PastClick>>

	@Query("SELECT * FROM pastWorkouts WHERE workoutTime > :lastWeek AND active ORDER BY workoutTime DESC")
	fun activePastClicksAndGoalLastWeek(
		lastWeek: Long = System.currentTimeMillis() - daysToMilliseconds(
			7
		)
	): Flow<List<PastClickAndGoal>>

	// Return number of active workouts by appWidgetId
	@Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:goalUid AND active='1'")
	suspend fun getCountOfActivePastWorkouts(goalUid: Int): Int

	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastClick::class)
	suspend fun insertPastWorkout(pastClick: PastClick)

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = PastClick::class)
	suspend fun updatePastWorkout(pastClick: PastClick)
}