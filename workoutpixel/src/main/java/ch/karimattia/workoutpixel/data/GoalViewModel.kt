package ch.karimattia.workoutpixel.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.core.GoalSaveActions
import ch.karimattia.workoutpixel.core.GoalWidgetActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
	private val goalRepository: GoalRepository,
	private val goalWidgetActionsFactory: GoalWidgetActions.Factory,
	// private val settingsRepository: SettingsRepository
) : ViewModel() {
	private fun goalWidgetActions(goal: Goal): GoalWidgetActions = goalWidgetActionsFactory.create(goal)

	val allGoals: LiveData<List<Goal>> = goalRepository.allGoals.asLiveData()

	private val _currentGoalUid = MutableLiveData(-1)
	val currentGoalUid: LiveData<Int> = _currentGoalUid
	fun changeCurrentGoalUid(goal: Goal?) {
		if (goal != null) {
			_currentGoalUid.value = goal.uid
		} else {
			_currentGoalUid.value = -1
		}
	}

	//val settingsData: LiveData<SettingsData> = settingsRepository.getSettings().asLiveData()

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
