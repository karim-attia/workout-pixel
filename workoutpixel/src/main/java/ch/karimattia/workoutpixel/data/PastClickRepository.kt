package ch.karimattia.workoutpixel.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PastClickRepository @Inject constructor(
	private val goalDao: GoalDao,
) {
	fun pastClicksByGoalUid(goalUid: Int): Flow<List<PastClick>> = goalDao.loadPastWorkoutsByGoalUid(goalUid = goalUid)
	fun pastClicksLastWeek(): Flow<List<PastClick>> = goalDao.pastClicksLastWeek()
	suspend fun getCountOfActivePastWorkouts(goalUid: Int): Int = goalDao.getCountOfActivePastWorkouts(goalUid = goalUid)
	suspend fun insertPastClick(pastClick: PastClick) = goalDao.insertPastWorkout(pastClick = pastClick)
	suspend fun updatePastClick(pastClick: PastClick) = goalDao.updatePastWorkout(pastClick)
}