package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*
class PastClickRepositoryOld(application: Application?, goalUid: Int) {
	val db: AppDatabase = AppDatabase.getDatabase(application)
	private val goalDao: GoalDao = db.goalDao()

	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	val allPastClicks: Flow<List<PastWorkout>> = goalDao.loadAllPastWorkoutsFlow(goalUid)
	suspend fun updatePastWorkout(pastWorkout: PastWorkout) {
		goalDao.updatePastWorkout(pastWorkout)
	}
}
*/

class PastClickRepository @Inject constructor(
	private val goalDao: GoalDao
) {
	// val db: AppDatabase = AppDatabase.getDatabase(application)
	// private val goalDao: GoalDao = db.goalDao()

	// Room executes all queries on a separate thread.
	// Observed Flow will notify the observer when the data has changed.

	fun allPastClicks(goalUid: Int): Flow<List<PastWorkout>> = goalDao.loadAllPastWorkoutsFlow(goalUid)

	fun updatePastWorkout(pastWorkout: PastWorkout) {
		goalDao.updatePastWorkout(pastWorkout)
	}

	fun getCountOfActivePastWorkouts(goalUid: Int): Int {
		return goalDao.getCountOfActivePastWorkouts(goalUid)
	}

	fun insertClickedWorkout(widgetUid: Int, thisWorkoutTime: Long) {
		goalDao.insertPastWorkout(PastWorkout(widgetUid, thisWorkoutTime))
		// CommonFunctions.executorService.execute { goalDao.insertPastWorkout(clickedWorkout) }
	}
}