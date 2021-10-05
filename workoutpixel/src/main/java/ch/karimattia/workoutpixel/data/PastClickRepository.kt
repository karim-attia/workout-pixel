package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PastClickRepository @Inject constructor(
	private val goalDao: GoalDao
) {
	fun pastClicksByGoalUid(goalUid: Int): Flow<List<PastClick>> = goalDao.loadAllPastWorkoutsFlow(goalUid)

	fun updatePastWorkout(pastClick: PastClick) {
		goalDao.updatePastWorkout(pastClick)
	}

	fun getCountOfActivePastWorkouts(goalUid: Int): Int {
		return goalDao.getCountOfActivePastWorkouts(goalUid = goalUid)
	}

	fun insertPastClick(pastClick: PastClick) {
		goalDao.insertPastWorkout(pastClick)
	}
}