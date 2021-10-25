package ch.karimattia.workoutpixel.screens.progress

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.data.PastClick
import ch.karimattia.workoutpixel.data.PastClickRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private const val TAG: String = "ProgressViewModel"

@HiltViewModel
class ProgressViewModel @Inject constructor(
	var pastClickRepository: PastClickRepository,
) : ViewModel() {
	private val pastClicksFlow: Flow<List<PastClick>> = pastClickRepository.pastClicksLastWeek()

/*
	fun updatePastClick(pastClick: PastClick) = viewModelScope.launch {
		pastClickRepository.updatePastClick(pastClick)
	}
*/

	val pastClicks: SnapshotStateList<PastClick> = mutableStateListOf()

	// This is needed so the state of pastClicks and thus the UI updates.
	init {
		viewModelScope.launch {
			pastClicksFlow.collectLatest { newPastClicks ->
				pastClicks.clear()
				pastClicks.addAll(newPastClicks)
			}
		}
	}
}