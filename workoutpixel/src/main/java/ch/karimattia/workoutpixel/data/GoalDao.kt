package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
	// Goals
	// Get goal by appWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
	suspend fun loadGoalByAppWidgetId(appWidgetId: Int): Goal?

	// Get goal by uid
	@Query("SELECT * FROM goals WHERE uid=:uid")
	suspend fun loadGoalByUid(uid: Int): Goal

	// Get goal by uid
	@Query("SELECT * FROM goals WHERE appWidgetId=:appWidgetId")
	fun loadGoalByAppWidgetIdFlow(appWidgetId: Int): Flow<Goal>

	// Get all goals
	@Query("SELECT * FROM goals")
	fun loadAllGoalsFlow(): Flow<List<Goal>>

	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
	suspend fun insertGoal(goal: Goal): Long

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = Goal::class)
	suspend fun updateGoal(goal: Goal)

	@Query("UPDATE goals SET appWidgetId =:invalidAppWidgetId WHERE uid=:uid")
	suspend fun setAppWidgetIdToNullByUid(uid: Int, invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID)

	@Query("UPDATE goals SET appWidgetId =:invalidAppWidgetId WHERE appWidgetId=:appWidgetId")
	suspend fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int, invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID)

	// Get all goals without AppWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId =:invalidAppWidgetId")
	fun loadGoalsWithoutValidAppWidgetId(invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): Flow<List<Goal>>

	// Get all goals with AppWidgetId
	@Query("SELECT * FROM goals WHERE appWidgetId !=:invalidAppWidgetId")
	suspend fun loadGoalsWithValidAppWidgetId(invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID): List<Goal>

	@Delete
	suspend fun deleteGoal(goal: Goal)


	// Past Workouts
	@Query("SELECT * FROM pastWorkouts WHERE widgetUid=:goalUid ORDER BY workoutTime DESC")
	fun loadAllPastWorkoutsFlow(goalUid: Int): Flow<List<PastClick>>

	// Return number of active workouts by appWidgetId
	@Query("SELECT COUNT() FROM pastWorkouts WHERE widgetUid=:goalUid AND active='1'")
	suspend fun getCountOfActivePastWorkouts(goalUid: Int): Int

	@Insert(onConflict = OnConflictStrategy.REPLACE, entity = PastClick::class)
	suspend fun insertPastWorkout(pastClick: PastClick)

	@Update(onConflict = OnConflictStrategy.REPLACE, entity = PastClick::class)
	suspend fun updatePastWorkout(pastClick: PastClick)
}