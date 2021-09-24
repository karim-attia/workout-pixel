package ch.karimattia.workoutpixel.database

import android.app.Application
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class PastClickViewModel @Inject constructor(
	private val repository: PastClickRepository
) : ViewModel() {
	fun pastClicks(goalUid: Int): LiveData<List<PastWorkout>> = repository.allPastClicks(goalUid = goalUid).asLiveData()
	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}
}

/*class PastClickViewModelFactory(private val application: Application, private val goalUid: Int) : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		return PastClickViewModelOld(application, goalUid) as T
	}
}*/

/*
@AssistedFactory
interface PastClickViewModelAssistedFactory {
	fun create(goalUid: Int): PastClickViewModel
	fun provideFactory(
		assistedFactory: PastClickViewModelAssistedFactory, // 1
		goalUid: Int,
	): ViewModelProvider.Factory =
		object : ViewModelProvider.Factory {
			@Suppress("UNCHECKED_CAST")
			override fun <T : ViewModel?> create(modelClass: Class<T>): T {
				return assistedFactory.create(goalUid) as T // 2
			}
		}
}

*/
