package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Suppress("unused")
private const val TAG = "GoalRepository"

class GoalRepository @Inject constructor(
	private val goalDao: GoalDao,
) {
	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allGoals: Flow<List<Goal>> = goalDao.loadAllGoalsFlow()
	//val goalsWithInvalidOrNullAppWidgetId: Flow<List<Goal>> = goalDao.loadGoalsWithInvalidOrNullAppWidgetId()

	suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal = goal)
	suspend fun insertGoal(goal: Goal): Int = goalDao.insertGoal(goal = goal).toInt()
	suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal = goal)
	suspend fun loadGoalByUid(uid: Int): Goal = goalDao.loadGoalByUid(uid = uid)
	fun loadGoalByAppWidgetIdFlow(goal: Goal): Flow<Goal> = goalDao.loadGoalByAppWidgetIdFlow(goal.appWidgetId)
	/*{
		return if (goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) flowOf(goal)
		else goalDao.loadGoalByAppWidgetIdFlow(goal.appWidgetId)
	}*/
	fun loadGoalsWithoutValidAppWidgetId(): Flow<List<Goal>> = goalDao.loadGoalsWithoutValidAppWidgetId()
	suspend fun loadGoalsWithValidAppWidgetId(): List<Goal> = goalDao.loadGoalsWithValidAppWidgetId()
	suspend fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int) = goalDao.setAppWidgetIdToNullByAppwidgetId(appWidgetId = appWidgetId)
	suspend fun loadGoalByAppWidgetId(appWidgetId: Int): Goal = goalDao.loadGoalByAppWidgetId(appWidgetId = appWidgetId)
	suspend fun setAppWidgetIdToNullByUid(uid: Int) = goalDao.setAppWidgetIdToNullByUid(uid = uid)
}