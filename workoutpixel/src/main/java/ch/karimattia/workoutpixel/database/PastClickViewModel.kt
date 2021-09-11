package ch.karimattia.workoutpixel.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PastClickViewModel(application: Application?, goalUid: Int) : ViewModel() {
	private val repository: PastClickRepository = PastClickRepository(application, goalUid)
	val pastClicks: LiveData<List<PastWorkout>> = repository.allPastClicks.asLiveData()

	fun updatePastClick(pastClick: PastWorkout) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}
}