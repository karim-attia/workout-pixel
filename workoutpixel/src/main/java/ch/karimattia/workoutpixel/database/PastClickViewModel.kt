package ch.karimattia.workoutpixel.database

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class PastClickViewModel(application: Application?, goalUid: Int) : ViewModel() {
	private val repository: PastClickRepository = PastClickRepository(application = application, goalUid = goalUid)
	val pastClicks: LiveData<List<PastWorkout>> = repository.allPastClicks.asLiveData()

	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}
}

class PastClickViewModelFactory(private val application: Application, private val goalUid: Int) : ViewModelProvider.Factory {
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		return PastClickViewModel(application, goalUid) as T
	}
}
