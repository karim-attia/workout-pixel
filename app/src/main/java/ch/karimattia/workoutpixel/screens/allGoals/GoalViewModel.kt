package ch.karimattia.workoutpixel.screens.allGoals

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.core.Constants
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private const val TAG: String = "GoalViewModel"

@HiltViewModel
class GoalViewModel @Inject constructor(
	private val goalRepository: GoalRepository,
) : ViewModel() {
	private val allGoalsFlow: Flow<List<Goal>> = goalRepository.allGoals
	val allGoals: SnapshotStateList<Goal> = mutableStateListOf()

	// This is needed so the state of allGoals and thus the UI updates.
	init {
		viewModelScope.launch {
			allGoalsFlow.collect { newGoals ->
				allGoals.clear()
				allGoals.addAll(newGoals)
			}
		}
	}

	private val _currentGoalUid = MutableLiveData(Constants.INVALID_GOAL_UID)
	val currentGoalUid: LiveData<Int> = _currentGoalUid
	fun changeCurrentGoalUid(goal: Goal?) {
		if (goal != null) changeCurrentGoalUid(goal.uid)
		else changeCurrentGoalUid(Constants.INVALID_GOAL_UID)
	}

	fun changeCurrentGoalUid(goalUid: Int) { _currentGoalUid.value = goalUid }

	suspend fun deleteGoal(goal: Goal) = goalRepository.deleteGoal(goal)
	suspend fun insertGoal(goal: Goal): Int = goalRepository.insertGoal(goal)
	suspend fun updateGoal(goal: Goal) = goalRepository.updateGoal(goal)

}
