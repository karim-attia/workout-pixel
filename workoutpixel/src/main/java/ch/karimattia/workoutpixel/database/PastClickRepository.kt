package ch.karimattia.workoutpixel.database

import android.app.Application
import kotlinx.coroutines.flow.Flow

class PastClickRepository(application: Application?, goalUid: Int) {
	val db: AppDatabase = AppDatabase.getDatabase(application)
	private val goalDao: GoalDao = db.goalDao()

	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allPastClicks: Flow<List<PastWorkout>> = goalDao.loadAllPastWorkoutsFlow(goalUid)

	suspend fun updatePastWorkout(pastWorkout: PastWorkout) {
		goalDao.updatePastWorkout(pastWorkout)
	}
}