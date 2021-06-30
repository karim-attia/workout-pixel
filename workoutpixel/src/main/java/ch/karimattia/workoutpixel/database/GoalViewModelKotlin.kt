package ch.karimattia.workoutpixel.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import kotlinx.coroutines.flow.Flow

class GoalViewModelKotlin(private val repositoryKotlin: GoalRepositoryKotlin) : ViewModel() {
    private val TAG = "InteractWithWidgetInDb"

    val allGoals: LiveData<List<Goal>> = repositoryKotlin.allGoalsFlow.asLiveData()

    fun updateGoal(context: Context?, goal: Goal) {
        Log.d(TAG, "executorService updateWidget " + goal.debugString())
        CommonFunctions.executorService.execute { workoutDao(context).updateGoal(goal) }
    }

    fun saveDuringInitialize(context: Context?, goal: Goal): Int {
        Log.d(TAG, "executorService saveDuringInitialize " + goal.debugString())
        return workoutDao(context).insertGoal(goal)
            .toInt()
    }

    // --Commented out by Inspection START (23.06.21, 20:47):
    //    public static Goal loadGoalByAppWidgetId(Context context, Integer appWidgetId) {
    //        Log.d(TAG, "getPastWorkoutsFromDbByAppWidgetId" + appWidgetId);
    //        return workoutDao(context).loadGoalByAppWidgetId(appWidgetId);
    //    }
    // --Commented out by Inspection STOP (23.06.21, 20:47)
    fun loadGoalByUid(context: Context?, uid: Int): Goal {
        Log.d(TAG, "loadGoalByUid $uid")
        return workoutDao(context).loadGoalByUid(uid)
    }

    fun liveDataGoalByUid(context: Context?, uid: Int): LiveData<Goal> {
        Log.d(TAG, "getPastWorkoutsFromDbByUid $uid")
        return workoutDao(context).liveDataGoalByUid(uid)
    }

    fun loadAllGoalsFlow(context: Context?): Flow<List<Goal>> {
        Log.d(TAG, "loadAllGoalsFlow")
        return workoutDao(context).loadAllGoalsFlow()
    }

    suspend fun loadAllGoalsCoroutine(context: Context?): List<Goal> {
        Log.d(TAG, "loadAllGoalsCoroutine")
        return workoutDao(context).loadAllGoalsCoroutine()
    }

    fun loadAllGoals(context: Context?): List<Goal> {
        Log.d(TAG, "loadAllGoals")
        return workoutDao(context).loadAllGoals()
    }

    fun loadAllGoalsLiveData(context: Context?): LiveData<List<Goal>> {
        Log.d(TAG, "loadAllGoals")
        return workoutDao(context).loadAllGoalsLiveData()
    }

    fun setAppWidgetIdToNullByAppwidgetId(context: Context?, appWidgetId: Int) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByAppwidgetId$appWidgetId")
        CommonFunctions.executorService.execute {
            workoutDao(context).setAppWidgetIdToNullByAppwidgetId(
                appWidgetId
            )
        }
    }

    fun setAppWidgetIdToNullByUid(context: Context?, uid: Int) {
        Log.d(TAG, "executorService setAppWidgetIdToNullByUid: $uid")
        CommonFunctions.executorService.execute {
            workoutDao(context).setAppWidgetIdToNullByUid(
                uid
            )
        }
    }

    fun loadGoalsWithoutValidAppWidgetId(context: Context?): List<Goal> {
        Log.d(TAG, "loadGoalsWithoutValidAppWidgetId")
        return workoutDao(context).loadGoalsWithoutValidAppWidgetId()
    }

    fun loadGoalsWithValidAppWidgetId(context: Context?): List<Goal> {
        Log.d(TAG, "loadGoalsWithValidAppWidgetId")
        return workoutDao(context).loadGoalsWithValidAppWidgetId()
    }

    fun deleteGoal(context: Context?, goal: Goal) {
        Log.d(TAG, "executorService deleteGoal: " + goal.debugString())
        CommonFunctions.executorService.execute { workoutDao(context).deleteGoal(goal) }
    }

    fun getCountOfGoals(context: Context?): Int {
        Log.d(TAG, "getCountOfGoals")
        return workoutDao(context).countOfGoals
    }

    fun workoutDao(context: Context?): GoalDaoKotlin {
        val db = AppDatabase.getDatabase(context)
        return db.workoutDaoKotlin()
    }
}

class GoalViewModelFactory(private val repositoryKotlin: GoalRepositoryKotlin) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModelKotlin::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalViewModelKotlin(repositoryKotlin) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
