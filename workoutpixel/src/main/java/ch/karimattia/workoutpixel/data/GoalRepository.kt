package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Suppress("unused") private const val TAG = "GoalRepository"

class GoalRepository @Inject constructor(
	private val goalDao: GoalDao
) {
	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allGoals: Flow<List<Goal>> = goalDao.loadAllGoalsFlow()
	//val goalsWithInvalidOrNullAppWidgetId: Flow<List<Goal>> = goalDao.loadGoalsWithInvalidOrNullAppWidgetId()

	fun updateGoal(goal: Goal) {
		goalDao.updateGoal(goal)
	}

	fun deleteGoal(goal: Goal) {
		goalDao.deleteGoal(goal)
	}

	fun insertGoal(goal: Goal): Long {
		return goalDao.insertGoal(goal)
	}

	fun loadGoalByUid(uid: Int): Goal {
		return goalDao.loadGoalByUid(uid)
	}

	fun loadGoalsWithoutValidAppWidgetId(): Flow<List<Goal>> {
		return goalDao.loadGoalsWithoutValidAppWidgetId()
	}

	fun loadGoalsWithValidAppWidgetId(): List<Goal> {
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