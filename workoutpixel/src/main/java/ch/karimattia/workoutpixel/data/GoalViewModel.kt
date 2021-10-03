package ch.karimattia.workoutpixel.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.core.GoalWidgetActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
	private val goalRepository: GoalRepository,
	private val goalWidgetActionsFactory: GoalWidgetActions.Factory,
) : ViewModel() {
	private fun goalWidgetActions(goal: Goal): GoalWidgetActions = goalWidgetActionsFactory.create(goal)

	val allGoalsFlow: Flow<List<Goal>> = goalRepository.allGoals
	val allGoals: SnapshotStateList<Goal> = mutableStateListOf()
	// This is needed so the state of allGoals and thus the UI updates.
	init {
		viewModelScope.launch {
			allGoalsFlow.collect { newGoals ->
				allGoals.clear()
				allGoals.addAll(newGoals)
/*
				for (goal in newGoals) {
					allGoals.add(goal)
				}
*/
			}
		}
	}

	private val _currentGoalUid = MutableLiveData(-1)
	val currentGoalUid: LiveData<Int> = _currentGoalUid
	fun changeCurrentGoalUid(goal: Goal?) {
		if (goal != null) {
			_currentGoalUid.value = goal.uid
		} else {
			_currentGoalUid.value = -1
		}
	}

	fun updateGoal(goal: Goal) = viewModelScope.launch {
		goalRepository.updateGoal(goal)
		Log.d("KotlinGoalViewModel: updateGoal: ", "$goal")
	}

	fun deleteGoal(goal: Goal) = viewModelScope.launch {
		goalRepository.deleteGoal(goal)
		Log.d("KotlinGoalViewModel: deleteGoal: ", "$goal")
	}

	fun insertGoal(goal: Goal) = viewModelScope.launch {
		goal.uid = goalRepository.insertGoal(goal).toInt()
		goalWidgetActions(goal = goal).runUpdate(true)
	}
}
