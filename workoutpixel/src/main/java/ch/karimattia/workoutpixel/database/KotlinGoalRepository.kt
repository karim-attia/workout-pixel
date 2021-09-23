package ch.karimattia.workoutpixel.database

import ch.karimattia.workoutpixel.core.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KotlinGoalRepository @Inject constructor(
	// application: Application,
	private val goalDao: GoalDao
) {
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

}