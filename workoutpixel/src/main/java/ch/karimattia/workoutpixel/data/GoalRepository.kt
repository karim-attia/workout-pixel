package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalRepository @Inject constructor(
	// application: Application,
	private val goalDao: GoalDao
) {
	private val tag = this.toString()
	// private val db: AppDatabase = AppDatabase.getDatabase(application)
	// private val goalDao: GoalDao = db.goalDao()

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

	fun loadAllGoals(): List<Goal> {
		return goalDao.loadAllGoals()
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