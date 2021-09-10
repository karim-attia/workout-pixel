package ch.karimattia.workoutpixel.database

import android.app.Application
import ch.karimattia.workoutpixel.core.Goal
import kotlinx.coroutines.flow.Flow

class KotlinGoalRepository(application: Application?) {
	val db: AppDatabase = AppDatabase.getDatabase(application)
	private val goalDao: GoalDao = db.goalDao()

	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allGoals: Flow<List<Goal>> = goalDao.loadAllGoalsFlow()
	//val goalsWithInvalidOrNullAppWidgetId: Flow<List<Goal>> = goalDao.loadGoalsWithInvalidOrNullAppWidgetId()

	suspend fun updateGoal(goal: Goal) {
		goalDao.updateGoal(goal)
	}

	suspend fun deleteGoal(goal: Goal) {
		goalDao.deleteGoal(goal)
	}

	suspend fun insertGoal(goal: Goal): Long {
		return goalDao.insertGoal(goal)
	}

}