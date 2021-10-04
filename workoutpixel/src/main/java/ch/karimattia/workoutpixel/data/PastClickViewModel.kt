package ch.karimattia.workoutpixel.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG: String = "PastClickViewModel"

// @HiltViewModel //: https://stackoverflow.com/questions/68649447/viewmodel-constructor-should-be-annotated-with-inject-instead-of-assistedinjec
class PastClickViewModel @AssistedInject constructor(
	private val repository: PastClickRepository,
	@Assisted var goalUid: Int,
) : ViewModel() {
	private fun pastClicksFlow(): Flow<List<PastClick>> = repository.pastClicksByGoalUid(goalUid = goalUid)
	fun updatePastClick(pastClick: PastClick) = viewModelScope.launch {
		repository.updatePastWorkout(pastClick)
	}

	val pastClicks: SnapshotStateList<PastClick> = mutableStateListOf()

	// This is needed so the state of pastClicks and thus the UI updates.
	init {
		viewModelScope.launch {
			pastClicksFlow().collect { newPastClicks ->
				pastClicks.clear()
				pastClicks.addAll(newPastClicks)
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