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

	suspend fun updateGoal(goal: Goal) {
		goalDao.updateGoal(goal)
	}

	suspend fun insertGoal(goal: Goal) {
		goalDao.insertGoal(goal)
	}

}