package ch.karimattia.workoutpixel.database

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.database.PastWorkout
import ch.karimattia.workoutpixel.database.PastClickRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class KotlinGoalViewModel(application: Application) : AndroidViewModel(application) {
	private val repository: KotlinGoalRepository = KotlinGoalRepository(application)
	val allGoals: LiveData<List<Goal>> = repository.allGoals.asLiveData()

	private val internalGoalUid = MutableLiveData(-1)
	val currentGoalUid: LiveData<Int> = internalGoalUid

	fun changeCurrentGoal(goal: Goal?) {
		if (goal != null) {
			internalGoalUid.value = goal.uid
		}
		else {internalGoalUid.value = -1}
	}

/*
	fun changeCurrentGoal2(goal: Goal) {
		currentGoalUid = allGoals.value?.indexOf(goal)
	}

	val currentGoal:Goal?
		get() = allGoals.getOrNull(currentGoalUid)

	fun currentGoal(goalUid: Int): LiveData<Goal> = repository.allGoals
 */

	fun updateGoal(goal: Goal) = viewModelScope.launch {
		repository.updateGoal(goal)
		Log.d("KotlinGoalViewModel: updateGoal: ", "$goal")
	}

	fun insertGoal(goal: Goal) = viewModelScope.launch {
		repository.insertGoal(goal)
	}

}