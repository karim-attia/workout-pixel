package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Suppress("unused") private const val TAG = "GoalRepository"

class GoalRepository @Inject constructor(
	private val goalDao: GoalDao
) {
	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allGoals: Flow<List<Goal>> = goalDao.loadAllGoalsFlow()
	//val goalsWithInvalidOrNullAppWidgetId: Flow<List<Goal>> = goalDao.loadGoalsWithInvalidOrNullAppWidgetId()

	fun deleteGoal(goal: Goal) {
		goalDao.deleteGoal(goal)
	}

	suspend fun insertGoal(goal: Goal): Int {
		return goalDao.insertGoal(goal).toInt()
	}

	suspend fun loadGoalByUid(uid: Int): Goal {
		return goalDao.loadGoalByUid(uid)
	}

	fun loadGoalByAppWidgetIdFlow(goal: Goal): Flow<Goal> {
		return if (goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) flowOf(goal)
		else goalDao.loadGoalByAppWidgetIdFlow(goal.appWidgetId)
	}

	fun loadGoalsWithoutValidAppWidgetId(): Flow<List<Goal>> {
		return goalDao.loadGoalsWithoutValidAppWidgetId()
	}

	suspend fun loadGoalsWithValidAppWidgetId(): List<Goal> {
		return goalDao.loadGoalsWithValidAppWidgetId()
	}

	fun setAppWidgetIdToNullByAppwidgetId(appWidgetId: Int) {
		goalDao.setAppWidgetIdToNullByAppwidgetId(appWidgetId)
		// OldCommonFunctions.executorService.execute {goalDao.setAppWidgetIdToNullByAppwidgetId(appWidgetId)}
	}

	fun loadGoalByAppWidgetId(appWidgetId: Int): Goal {
		return goalDao.loadGoalByAppWidgetId(appWidgetId)
	}

	fun setAppWidgetIdToNullByUid(uid: Int) {
		goalDao.setAppWidgetIdToNullByUid(uid)
		// OldCommonFunctions.executorService.execute { OldGoalViewModel.workoutDao(context).setAppWidgetIdToNullByUid(uid) }
	}
}