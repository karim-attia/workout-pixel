package ch.karimattia.workoutpixel.database

import androidx.lifecycle.LiveData
import ch.karimattia.workoutpixel.core.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepositoryKotlin(private val workoutDao: GoalDaoKotlin) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allGoalsFlow: Flow<List<Goal>> = workoutDao.loadAllGoalsFlow()
    val allGoalsLiveData: LiveData<List<Goal>> = workoutDao.loadAllGoalsLiveData()

/*
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Goal) {
        wordDao.insert(word)
    }
*/
}
