package ch.karimattia.workoutpixel.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.core.GoalWidgetActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
	private val goalRepository: GoalRepository,
	// private val settingsRepository: SettingsRepository
) : ViewModel() {

	val allGoals: LiveData<List<Goal>> = goalRepository.allGoals.asLiveData()
	//val goalsWithInvalidOrNullAppWidgetId: LiveData<List<Goal>> = repository.goalsWithInvalidOrNullAppWidgetId.asLiveData()

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


/*	private val _currentGoal = MutableLiveData<Goal?>()
	val currentGoal: LiveData<Goal?> = _currentGoal
	fun changeCurrentGoal(goal: Goal?, goals: List<Goal>) {
		_currentGoal.value = goalFromGoalsByUid(goalUid = goal?.uid, goals = goals)
*//*
		if (goal != null) {
			_currentGoal.value = goalFromGoalsByUid(goalUid = goal.uid, goals = goals)
		} else {
			_currentGoal.value = null
		}
		*//*
	}*/

/*
	private val _appBarTitle = MutableLiveData("")
	val appBarTitle: LiveData<String> = _appBarTitle

	fun changeAppBarTitle(appBarTitle: String?) {
		if (appBarTitle != null) {
			_appBarTitle.value = appBarTitle
		} else {
			_appBarTitle.value = ""
		}
		Log.d("kotlinGoalViewModel.changeAppBarTitle: ", appBarTitle.toString())
	}
*/

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
		GoalWidgetActions(context = viewModelScope.coroutineContext as Context, goal = goal).runUpdate(true)
	}
}
