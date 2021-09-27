package ch.karimattia.workoutpixel.data

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
private const val TAG: String = "PastClickViewModel"

/*
@HiltViewModel
class KotlinGoalViewModel @Inject constructor(
	private val application: Application,
	private val repository: KotlinGoalRepository
) : ViewModel() {

}

class GoalSaveActions @AssistedInject constructor(
	@ApplicationContext val context: Context? = null,
	private val kotlinGoalRepository: KotlinGoalRepository? = null,
	@Assisted var goal: Goal,
) {
	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalSaveActions
	}

*/
/*
class PastClickViewModelOld (
	application: Application,
	goalUid: Int
) : ViewModel() {
	private val repository: PastClickRepositoryOld = PastClickRepositoryOld(application = application, goalUid = goalUid)
	val pastClicks: LiveData<List<PastWorkout>> = repository.allPastClicks.asLiveData()

	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}
}*/

// Works with goal as variable
/*@HiltViewModel
class PastClickViewModel @Inject constructor(
	private val repository: PastClickRepository
) : ViewModel() {
	fun pastClicks(goalUid: Int): LiveData<List<PastWorkout>> = repository.allPastClicks(goalUid = goalUid).asLiveData()
	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}
}*/

/*class PastClickViewModelFactory(private val application: Application, private val goalUid: Int) : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		return PastClickViewModelOld(application, goalUid) as T
	}
}*/

// @HiltViewModel //: https://stackoverflow.com/questions/68649447/viewmodel-constructor-should-be-annotated-with-inject-instead-of-assistedinjec
class PastClickViewModel @AssistedInject constructor(
	private val repository: PastClickRepository,
	@Assisted var goalUid: Int,
) : ViewModel() {
	private fun pastClicksFlow(): Flow<List<PastWorkout>> = repository.pastClicksByGoalUid(goalUid = goalUid)
	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}

	var pastClicks = mutableStateListOf<PastWorkout>() //Using an immutable list is recommended

	// This is needed so the state of pastClicks and thus the UI updates.
	init {
		viewModelScope.launch {
			pastClicksFlow().collect {pastWorkouts ->
				pastClicks.clear()
				for (workout in pastWorkouts) {
					pastClicks.add(workout)
				}
				Log.d(TAG, "collect")
			}
		}
	}
}

@AssistedFactory
interface PastClickViewModelAssistedFactory {
	fun create(goalUid: Int): PastClickViewModel
}

fun provideFactory(
	assistedFactory: PastClickViewModelAssistedFactory,
	goalUid: Int,
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		return assistedFactory.create(goalUid) as T
	}
}