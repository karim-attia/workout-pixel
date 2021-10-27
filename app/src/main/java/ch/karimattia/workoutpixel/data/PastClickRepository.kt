package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PastClickRepository @Inject constructor(
	private val goalDao: GoalDao,
) {
	fun pastClicksByGoalUid(goalUid: Int): Flow<List<PastClick>> = goalDao.loadPastWorkoutsByGoalUid(goalUid = goalUid)
	fun activePastClicksLastWeek(): Flow<List<PastClick>> = goalDao.activePastClicksLastWeek()
	fun activePastClicksAndGoalLastWeek(): Flow<List<PastClickAndGoal>> = goalDao.activePastClicksAndGoalLastWeek()
	suspend fun getCountOfActivePastWorkouts(goalUid: Int): Int = goalDao.getCountOfActivePastWorkouts(goalUid = goalUid)
	suspend fun insertPastClick(pastClick: PastClick) = goalDao.insertPastWorkout(pastClick = pastClick)
	suspend fun updatePastClick(pastClick: PastClick) = goalDao.updatePastWorkout(pastClick)
}