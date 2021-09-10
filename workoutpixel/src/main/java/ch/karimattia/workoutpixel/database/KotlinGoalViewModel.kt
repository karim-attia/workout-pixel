package ch.karimattia.workoutpixel.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.goalFromGoalsByUid
import kotlinx.coroutines.launch


class KotlinGoalViewModel(application: Application) : AndroidViewModel(application) {
	private val repository: KotlinGoalRepository = KotlinGoalRepository(application)
	val allGoals: LiveData<List<Goal>> = repository.allGoals.asLiveData()
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

	private val _currentGoal = MutableLiveData<Goal?>()
	val currentGoal: LiveData<Goal?> = _currentGoal
	fun changeCurrentGoal(goal: Goal?, goals: List<Goal>) {
		_currentGoal.value = goalFromGoalsByUid(goalUid = goal?.uid, goals = goals)
/*
		if (goal != null) {
			_currentGoal.value = goalFromGoalsByUid(goalUid = goal.uid, goals = goals)
		} else {
			_currentGoal.value = null
		}
		*/
	}

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


	fun updateGoal(goal: Goal) = viewModelScope.launch {
		repository.updateGoal(goal)
		Log.d("KotlinGoalViewModel: updateGoal: ", "$goal")
	}

	fun deleteGoal(goal: Goal) = viewModelScope.launch {
		repository.deleteGoal(goal)
		Log.d("KotlinGoalViewModel: deleteGoal: ", "$goal")
	}

	fun insertGoal(goal: Goal) = viewModelScope.launch {
		goal.uid = repository.insertGoal(goal).toInt()
		goal.runUpdate(getApplication<Application>().applicationContext, true)
	}
}